package tech.techdad.bghivefeeds.runtime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import tech.techdad.bghivefeeds.api.AuthHelper;
import tech.techdad.bghivefeeds.api.Channels;
import tech.techdad.bghivefeeds.api.Temperature;

import java.util.Map;
import java.util.TimerTask;


public class TemperatureRunnable extends TimerTask {

    private static final Logger LOGGER = LogManager.getLogger();

    AuthHelper auth = new AuthHelper();
    String sessionId = auth.getSessionID();

    @Override
    public void run() {
        LOGGER.debug("Starting thread...");
        LOGGER.debug(new ParameterizedMessage("Session Initialized with Session ID:{}", sessionId));

        if (sessionId != null) {

            Map<String, String> sessionHeaders = auth.getHttpHeaders(sessionId);

            Channels channel = new Channels();

            String tempChannel = channel.getTemperatureChannel(sessionHeaders);

            Temperature temp = new Temperature();

            int currentTemp = temp.getCurrentTemperature(sessionHeaders, tempChannel);

            LOGGER.debug(currentTemp);

        }
    }
}
