package handlers;


import tools.ServerLogger;

import java.util.Timer;
import java.util.TimerTask;


public class PingHandler extends TaskHandler {
    private boolean isPinged = false;

    public void handle() {
        isPinged = true;
        server.sendMessage("S:PONG");
    }

    public void initializeTimeoutTimer() {
        int timeout =  5 * 60 * 1000;

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(!isPinged)
                {
                    ServerLogger.logMessage("Utracono połączenie");
                }
                isPinged = false;
            }
        }, timeout, timeout);
    }
}
