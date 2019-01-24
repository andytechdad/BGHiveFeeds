package tech.techdad.bghivefeeds.properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertyHelper {

    private static final Logger LOGGER = LogManager.getLogger();

    public Map<String, String> getConnection() {
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

            LOGGER.debug(new ParameterizedMessage("Base URL from properties file is: {}", url));
            LOGGER.debug(new ParameterizedMessage("Using login: {}", username));

            dictionary.put("Username", username);
            dictionary.put("Password", password);
            dictionary.put("URL", url);

        } catch (IOException ex) {
            LOGGER.error(ex);
        } finally {

        }
        return dictionary;
    }

    public String getBgHiveURL() {

        Properties prop = new Properties();
        String url = null;

        try {

            InputStream input = new FileInputStream("application.properties");

            // load the properties file
            prop.load(input);

            // Log the properties to the Debug logger
            url = prop.getProperty("tech.techdad.bghivefeeds.bghiveurl");

            LOGGER.debug(new ParameterizedMessage("Base URL from properties file is: {}", url));

            return url;

        } catch (IOException ex) {
            LOGGER.error(ex);
        } finally {

        }

        return url;

    }

}
