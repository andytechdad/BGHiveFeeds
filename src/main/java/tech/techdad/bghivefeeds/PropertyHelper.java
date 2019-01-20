package tech.techdad.bghivefeeds;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertyHelper {

    private static final Logger LOGGER = LogManager.getLogger(PropertyHelper.class);

    public static Map<String, String> getConnection() {
        Properties prop = new Properties();

        InputStream input = null;

        //Dictionary for those variables
        Map<String, String> dictionary = new HashMap<>();

        try {

            input = new FileInputStream("application.properties");

            // load the properties file
            prop.load(input);

            // Log the properties to the Debug logger
            String username = prop.getProperty("tech.techdad.bghivefeeds.username");
            String url = prop.getProperty("tech.techdad.bghivefeeds.bghiveurl");
            String password = prop.getProperty("tech.techdad.bghivefeeds.password");

            LOGGER.debug(url);
            LOGGER.debug(username);

            dictionary.put("Username", username);
            dictionary.put("Password", password);
            dictionary.put("URL", url);

        } catch (IOException ex) {
            LOGGER.error(ex);
        } finally {

        }
        return dictionary;
    }

}