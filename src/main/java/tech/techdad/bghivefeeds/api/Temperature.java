package tech.techdad.bghivefeeds.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import tech.techdad.bghivefeeds.properties.PropertyHelper;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class Temperature {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String CONTENT = "Content-type";
    private static final String ACCEPT = "Accept";
    private static final String CLIENT = "X-Omnia-Client";
    private static final String TOKEN = "X-Omnia-Access-Token";

    public float getCurrentTemperature(Map<String, String> session, String channel) {

        float temperature = 0;

        //hive_url + "/channels/" + id + "?start=" + timethen + "&end=" + timenow + "&timeUnit=MINUTES&rate=1&operation=MAX"

        PropertyHelper prop = new PropertyHelper();

        String url = prop.getBgHiveURL();
        String content = session.get(CONTENT);
        String accept = session.get(ACCEPT);
        String omniaClient = session.get(CLIENT);
        String omniaAccessToken = session.get(TOKEN);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            Date today = Calendar.getInstance().getTime();
            SimpleDateFormat temperatureDate = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
            String currentTime = temperatureDate.format(today);
            try {

                Date date = temperatureDate.parse(currentTime);

                long timeNow = date.getTime();

                LOGGER.debug(timeNow);

                long timeThen = timeNow - 100000;

                LOGGER.debug(timeThen);

                String bgChannelsUrl = url + "/channels/" + channel + "?start=" + timeThen + "&end=" + timeNow + "&timeUnit=SECONDS&rate=5&operation=AVG";

                LOGGER.debug(bgChannelsUrl);

                HttpGet get = new HttpGet(bgChannelsUrl);

                get.addHeader(CONTENT, content);
                get.addHeader(ACCEPT, accept);
                get.addHeader(CLIENT, omniaClient);
                get.addHeader(TOKEN, omniaAccessToken);

                try {

                    HttpResponse response = httpClient.execute(get);
                    String responseText = response.toString();
                    HttpEntity responseStream = response.getEntity();
                    String responseBody = EntityUtils.toString(responseStream);
                    int responseCode = response.getStatusLine().getStatusCode();

                    if (responseCode == 200) {

                        JsonParser jsonParser = new JsonParser();

                        JsonElement temperatureTree = jsonParser.parse(responseBody);
                        JsonObject temperatureObject = temperatureTree.getAsJsonObject();
                        JsonElement temperatureChannels = temperatureObject.get("channels");
                        JsonArray temperatureValuesArray = temperatureChannels.getAsJsonArray();
                        JsonElement temperatureValuesKey = temperatureValuesArray.get(0);
                        JsonObject temperatureValuesObject = temperatureValuesKey.getAsJsonObject();
                        JsonElement temperatureValuesMap = temperatureValuesObject.get("values");
                        JsonObject temperatureValues = temperatureValuesMap.getAsJsonObject();

                        for (String temperatureKey : temperatureValues.keySet()) {
                            long keyEpoch = Long.parseLong(temperatureKey);
                            JsonElement valueTemperature = temperatureValues.get(temperatureKey);
                            String temperatureFinal = valueTemperature.getAsString();
                            LOGGER.info(new ParameterizedMessage("TIME: {} TEMP: {}", keyEpoch, valueTemperature));
                            temperature = Float.parseFloat(temperatureFinal);
                        }

                    } else {

                        LOGGER.debug("Temperature not found in poll");
                    }

                    } catch(HttpResponseException e){

                        LOGGER.error(e);
                    }

                } catch (ParseException e) {

                    LOGGER.error(e);

                }

        } catch (IOException e) {

            LOGGER.error(e.getMessage());

        }

        return temperature;
    }

}
