package de.abstractolotl.azplace.exceptions.websocket;

import de.abstractolotl.azplace.model.utility.WebSocketServerInfo;

public class ConnectionLostException extends Exception {

    private final WebSocketServerInfo serverInfo;

    public ConnectionLostException(WebSocketServerInfo serverInfo, Throwable cause) {
        super(cause);
        this.serverInfo = serverInfo;
    }

    public WebSocketServerInfo getServerInfo() {
        return serverInfo;
    }
}
