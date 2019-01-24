package tech.techdad.bghivefeeds;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import tech.techdad.bghivefeeds.api.AuthHelper;
import tech.techdad.bghivefeeds.api.Channels;
import tech.techdad.bghivefeeds.api.Nodes;
import tech.techdad.bghivefeeds.api.Temperature;

import java.util.HashMap;
import java.util.Map;

public class BGHiveFeeds {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] arguments) {

        AuthHelper auth = new AuthHelper();

        // Make initial connection to get the session ID / Access-Token
        String session = auth.getSessionID();

        if (session !=null) {
            LOGGER.debug(new ParameterizedMessage("Session Initialized with Session ID:{}", session));

            Map<String, String> sessionHeaders = auth.getHttpHeaders(session);

            LOGGER.debug(sessionHeaders);

            Nodes node = new Nodes();

            HashMap<String, String> nodes = node.getNodes(sessionHeaders);

            LOGGER.debug(nodes);

            Channels channel = new Channels();

            String channels = channel.getTemperatureChannel(sessionHeaders);

            LOGGER.debug(channels);

            Temperature temperature = new Temperature();

            int currentTemp = temperature.getCurrentTemperature(sessionHeaders, channels);

            LOGGER.debug(currentTemp);

        }
    }

}

