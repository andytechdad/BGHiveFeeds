package tech.techdad.bghivefeeds;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Nodes {

    private static final Logger LOGGER = LogManager.getLogger(Nodes.class);

    public static HashMap<String, String> getNodes(Map<String, String> session) {

        String url = PropertyHelper.getBgHiveURL();
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

            LOGGER.debug(responseCode);
            LOGGER.debug(responseText);
            LOGGER.debug(responseStream);
            LOGGER.debug(responseBody);


        }  catch (IOException e) {

            LOGGER.error(e.getMessage());

        }

        return nodes;
    }
}
