package tech.techdad.bghivefeeds;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

import java.util.HashMap;
import java.util.Map;

public class BGHiveFeeds {

    private static final Logger LOGGER = LogManager.getLogger(BGHiveFeeds.class);

    public static void main(String[] arguments) {

        Map<String, String> connection = PropertyHelper.getConnection();
        String session = AuthHelper.getSessionID(connection);

        if (session !=null) {
            LOGGER.debug(new ParameterizedMessage("Session Initialized with Session ID:{}", session));

            Map<String, String> sessionHeaders = AuthHelper.getHttpHeaders(session);

            LOGGER.debug(sessionHeaders);

            HashMap<String, String> nodes = Nodes.getNodes(sessionHeaders);

            LOGGER.debug(nodes);

        }
    }

}

