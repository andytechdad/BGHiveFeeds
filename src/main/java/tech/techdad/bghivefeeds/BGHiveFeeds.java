package tech.techdad.bghivefeeds;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Map;

public class BGHiveFeeds {

    private static final Logger LOGGER = LogManager.getLogger(BGHiveFeeds.class);

    public static void main(String[] arguments) {

        PropertyHelper property = new PropertyHelper();
        AuthHelper auth = new AuthHelper();

        Map<String, String> connection = property.getConnection();
        String session = auth.getSessionID(connection);

        LOGGER.debug(session);
    }

}

