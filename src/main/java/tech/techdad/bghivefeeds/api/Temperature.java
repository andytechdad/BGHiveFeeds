package tech.techdad.bghivefeeds.api;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    public int getCurrentTemperature(Map<String, String> session, String channel) {

        int temperature = 0;

        //hive_url + "/channels/" + id + "?start=" + timethen + "&end=" + timenow + "&timeUnit=MINUTES&rate=1&operation=MAX"

        PropertyHelper prop = new PropertyHelper();

        String url = prop.getBgHiveURL();
        String content = session.get(CONTENT);
        String accept = session.get(ACCEPT);
        String omniaClient = session.get(CLIENT);
        String omniaAccessToken = session.get(TOKEN);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            Date today = Calendar.getInstance().getTime();
            SimpleDateFormat temperatureDate = new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS zzz");
            String currentTime = temperatureDate.format(today);
            try {

                Date date = temperatureDate.parse(currentTime);

                long timeNow = date.getTime();

                LOGGER.debug(timeNow);

                long timeThen = timeNow - 240000;

                LOGGER.debug(timeThen);

                String bgChannelsUrl = url + "/channels/" + channel + "?start=" + timeThen + "&end=" + timeNow + "&timeUnit=SECONDS&rate=5&operation=AVG";

                LOGGER.debug(bgChannelsUrl);

                HttpGet get = new HttpGet(bgChannelsUrl);

                get.addHeader(CONTENT, content);
                get.addHeader(ACCEPT, accept);
                get.addHeader(CLIENT, omniaClient);
                get.addHeader(TOKEN, omniaAccessToken);

                HttpResponse response = httpClient.execute(get);
                String responseText = response.toString();
                HttpEntity responseStream = response.getEntity();
                String responseBody = EntityUtils.toString(responseStream);
                int responseCode = response.getStatusLine().getStatusCode();

                LOGGER.debug(responseBody);

            }  catch (ParseException e) {

            LOGGER.error(e);

            }

        } catch (IOException e) {

            LOGGER.error(e.getMessage());

        }

        return temperature;
    }

}
