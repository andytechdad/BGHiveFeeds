package tech.techdad.bghivefeeds.api;

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
import tech.techdad.bghivefeeds.PropertyHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Nodes {

    private static final Logger LOGGER = LogManager.getLogger();

    public HashMap<String, String> getNodes(Map<String, String> session) {

        PropertyHelper prop = new PropertyHelper();

        String url = prop.getBgHiveURL();
        String content = session.get("Content-type");
        String accept = session.get("Accept");
        String omniaClient = session.get("X-Omnia-Client");
        String omniaAccessToken = session.get("X-Omnia-Access-Token");

        HashMap<String, String> nodes = new HashMap<>();

        LOGGER.debug(url);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            String bgNodesUrl = url + "/nodes";
            LOGGER.debug(bgNodesUrl);

            HttpGet get          = new HttpGet(bgNodesUrl);

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

                JsonElement nodesTree = jsonParser.parse(responseBody);
                JsonObject nodesObject = nodesTree.getAsJsonObject();
                JsonElement nodesList = nodesObject.get("nodes");
                JsonArray nodesArray = nodesList.getAsJsonArray();
                int nodesLength = nodesArray.size();

                for (int nodeLength = 0; nodeLength < nodesLength; nodeLength++) {
                    JsonElement nodesArrayKey = nodesArray.get(nodeLength);
                    JsonObject nodesArrayObject = nodesArrayKey.getAsJsonObject();
                    JsonElement nodeName = nodesArrayObject.get("name");
                    LOGGER.debug(nodeName);
                }

            }


        }  catch (IOException e) {

            LOGGER.error(e.getMessage());

        }

        return nodes;
    }
}
