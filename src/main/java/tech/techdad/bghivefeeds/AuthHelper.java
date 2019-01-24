package tech.techdad.bghivefeeds;

import com.google.gson.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AuthHelper {

    private static final Logger LOGGER = LogManager.getLogger();
    // final Strings
    private static final String CONTENT = "application/vnd.alertme.zoo-6.6+json";
    private static final String ACCEPT = "application/vnd.alertme.zoo-6.6+json";
    private static final String CLIENT = "BGHiveFeeds";

    public String getSessionID() {

        PropertyHelper prop = new PropertyHelper();

        // Need username, password and URL from properties
        Map<String, String> connection = prop.getConnection();

        // these values should come from the map built in the property helper
        String bgUsername = connection.get("Username");
        String bgPassword = connection.get("Password");
        String bgConnectUrl = connection.get("URL");

        // what we ultimatley want to return
        String sessionID = null;

        // Request body needs a session object to get the sessionID
        Map<String, ArrayList<Map>> session = new HashMap<>();
        ArrayList<Map> objects = new ArrayList<>();
        Map<String, String> credentials = new HashMap<>();

        credentials.put("username", bgUsername);
        credentials.put("password", bgPassword);
        credentials.put("caller", "WEB");

        objects.add(credentials);

        // make the Map containing the object Array
        session.put("sessions", objects);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            // BG Hive API just accepts JSON with credentials
            // so we convert the Map to JSON wuth GsonBuilder
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();

            String bgSessionUrl = bgConnectUrl + "/auth/sessions";
            LOGGER.debug(bgSessionUrl);

            HttpPost post          = new HttpPost(bgSessionUrl);

            StringEntity postingString = new StringEntity(gson.toJson(session));

            post.setEntity(postingString);

            post.addHeader("Content-type", CONTENT );
            post.addHeader("Accept", ACCEPT);
            post.addHeader("X-Omnia-Client", CLIENT);

            HttpResponse response = httpClient.execute(post);
            int responseCode = response.getStatusLine().getStatusCode();

            LOGGER.debug(responseCode);

            if (responseCode == 200) {

                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    String content = EntityUtils.toString(entity);

                    JsonParser jsonParser = new JsonParser();

                    JsonElement jsonTree = jsonParser.parse(content);

                    if (jsonTree.isJsonObject()) {

                        JsonObject jsonObject = jsonTree.getAsJsonObject();
                        JsonElement sessionObject = jsonObject.get("sessions");
                        JsonArray sessionArray = sessionObject.getAsJsonArray();
                        JsonElement sessionArrayKey = sessionArray.get(0);
                        JsonObject sessionArrayObject = sessionArrayKey.getAsJsonObject();
                        sessionID = sessionArrayObject.get("sessionId").getAsString();

                        return sessionID;

                    } else {
                        LOGGER.error("Response received from API did not parse correctly");
                    }
                }

            } else {

                LOGGER.error("Non-200 HTTP response recieved");
                LOGGER.error(responseCode);
            }

        } catch (IOException e) {

            LOGGER.error(e.getMessage());

        } finally {

            LOGGER.debug(sessionID);

        }

        return sessionID;
    }

    public Map<String, String> getHttpHeaders(String sessionID){

        Map<String, String> httpHeaders = new HashMap<>();

        httpHeaders.put("Content-type", CONTENT);
        httpHeaders.put("Accept", ACCEPT);
        httpHeaders.put("X-Omnia-Client", CLIENT);
        httpHeaders.put("X-Omnia-Access-Token", sessionID);

        LOGGER.debug(httpHeaders);

        return httpHeaders;
    }


}
