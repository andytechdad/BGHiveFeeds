package tech.techdad.bghivefeeds;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Channels {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String CONTENT = "Content-type";
    private static final String ACCEPT = "Accept";
    private static final String CLIENT = "X-Omnia-Client";
    private static final String TOKEN = "X-Omnia-Access-Token";

    public HashMap<String, String> getChannels(Map<String, String> session) {

        PropertyHelper prop = new PropertyHelper();

        String url = prop.getBgHiveURL();
        String content = session.get(CONTENT);
        String accept = session.get(ACCEPT);
        String omniaClient = session.get(CLIENT);
        String omniaAccessToken = session.get(TOKEN);

        HashMap<String, String> channels = new HashMap<>();

        LOGGER.debug(url);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            String bgChannelsUrl = url + "/channels";
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

            if (responseCode == 200) {

                JsonParser jsonParser = new JsonParser();

                JsonElement channelsTree = jsonParser.parse(responseBody);
                JsonObject channelsObject = channelsTree.getAsJsonObject();
                JsonElement channelsList = channelsObject.get("channels");
                JsonArray channelsArray = channelsList.getAsJsonArray();
                int channelsLength = channelsArray.size();

                for (int channelLength = 0; channelLength < channelsLength; channelLength++) {
                    JsonElement channelsArrayKey = channelsArray.get(channelLength);
                    JsonObject channelsArrayObject = channelsArrayKey.getAsJsonObject();
                    JsonElement channelName = channelsArrayObject.get("id");
                    JsonElement channelUnit = channelsArrayObject.get("unit");
                    LOGGER.debug(new ParameterizedMessage("ID: {} UNIT: {}", channelName, channelUnit));
                }

            }


        } catch (IOException e) {

            LOGGER.error(e.getMessage());

        }

        return channels;
    }

    public String getTemperatureChannel(Map<String, String> session) {

        PropertyHelper prop = new PropertyHelper();

        String url = prop.getBgHiveURL();
        String content = session.get(CONTENT);
        String accept = session.get(ACCEPT);
        String omniaClient = session.get(CLIENT);
        String omniaAccessToken = session.get(TOKEN);

        String temperatureChannel = new String();

        LOGGER.debug(url);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            String bgChannelsUrl = url + "/channels";
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

            if (responseCode == 200) {

                JsonParser jsonParser = new JsonParser();

                JsonElement channelsTree = jsonParser.parse(responseBody);
                JsonObject channelsObject = channelsTree.getAsJsonObject();
                JsonElement channelsList = channelsObject.get("channels");
                JsonArray channelsArray = channelsList.getAsJsonArray();
                int channelsLength = channelsArray.size();

                for (int channelLength = 0; channelLength < channelsLength; channelLength++) {
                    JsonElement channelsArrayKey = channelsArray.get(channelLength);
                    JsonObject channelsArrayObject = channelsArrayKey.getAsJsonObject();
                    String channelName = channelsArrayObject.get("id").getAsString();
                    if (channelName.startsWith("temperature@")) {
                        LOGGER.debug(new ParameterizedMessage("ID: {}", channelName));
                        temperatureChannel = channelName;
                    }

                }

            }


        } catch (IOException e) {

            LOGGER.error(e.getMessage());

        }

        return temperatureChannel;

    }

}
