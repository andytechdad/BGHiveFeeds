package tech.techdad.bghivefeeds;

import com.google.gson.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
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

    private static final Logger LOGGER = LogManager.getLogger(AuthHelper.class);

    public static String getSessionID(Map<String, String> connection) {

        String bgUsername = connection.get("Username");
        String bgPassword = connection.get("Password");
        String bgConnectUrl = connection.get("URL");
        String sessionID = null;

        Map<String, ArrayList<Map>> session = new HashMap<>();
        ArrayList<Map> objects = new ArrayList<>();
        Map<String, String> credentials = new HashMap<>();

        credentials.put("username", bgUsername);
        credentials.put("password", bgPassword);
        credentials.put("caller", "WEB");

        objects.add(credentials);

        session.put("sessions", objects);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            CredentialsProvider provider = new BasicCredentialsProvider();
            UsernamePasswordCredentials httpCredentials = new UsernamePasswordCredentials(bgUsername, bgPassword);
            provider.setCredentials(AuthScope.ANY, httpCredentials);

            HttpClientContext context = HttpClientContext.create();
            context.setCredentialsProvider(provider);

            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            String json = gson.toJson(session);

            String bgSessionUrl = bgConnectUrl + "/auth/sessions";
            LOGGER.debug(bgSessionUrl);

            HttpPost post          = new HttpPost(bgSessionUrl);

            StringEntity postingString = new StringEntity(gson.toJson(session));

            post.setEntity(postingString);

            post.addHeader("Content-type", "application/vnd.alertme.zoo-6.6+json");
            post.addHeader("Accept","application/vnd.alertme.zoo-6.6+json");
            post.addHeader("X-Omnia-Client", "BGHiveFeeds");

            HttpResponse response = httpClient.execute(post);

            HttpEntity entity = response.getEntity();


            if (entity != null) {
                    String content =  EntityUtils.toString(entity);

                    JsonParser jsonParser = new JsonParser();

                    JsonElement jsonTree = jsonParser.parse(content);

                    LOGGER.debug(jsonTree);

                    if(jsonTree.isJsonObject()) {
                        JsonObject jsonObject = jsonTree.getAsJsonObject();

                        LOGGER.debug(jsonObject);

                        JsonElement sessionObject = jsonObject.get("sessions");
                        JsonArray sessionArray = sessionObject.getAsJsonArray();

                        LOGGER.debug(sessionArray.isJsonArray());
                        LOGGER.debug(sessionArray);

                        JsonElement sessionArrayKey = sessionArray.get(0);

                        LOGGER.debug(sessionArrayKey);

                        JsonObject sessionArrayObject = sessionArrayKey.getAsJsonObject();

                        LOGGER.debug(sessionArrayObject);

                        sessionID = sessionArrayObject.get("sessionId").getAsString();

                        LOGGER.debug(sessionID);

                        return sessionID;

                    }

            }

        } catch (IOException e) {
            LOGGER.debug(e.getMessage());
        } finally {
            LOGGER.debug("end");
        }

        return sessionID;
    }




}
