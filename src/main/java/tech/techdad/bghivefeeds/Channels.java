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

    public HashMap<String, String> getChannels(Map<String, String> session) {

        PropertyHelper prop = new PropertyHelper();

        String url = prop.getBgHiveURL();
        String content = session.get("Content-type");
        String accept = session.get("Accept");
        String omniaClient = session.get("X-Omnia-Client");
        String omniaAccessToken = session.get("X-Omnia-Access-Token");

        HashMap<String, String> channels = new HashMap<>();

        LOGGER.debug(url);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            String bgChannelsUrl = url + "/channels";
            LOGGER.debug(bgChannelsUrl);

            HttpGet get          = new HttpGet(bgChannelsUrl);

            get.addHeader("Content-type", content);
            get.addHeader("Accept",accept);
            get.addHeader("X-Omnia-Client", omniaClient);
            get.addHeader("X-Omnia-Access-Token", omniaAccessToken);

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


        }  catch (IOException e) {

            LOGGER.error(e.getMessage());

        }

        return channels;
    }
}
