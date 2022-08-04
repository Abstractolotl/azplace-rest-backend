package de.abstractolotl.azplace.websocket;

import de.abstractolotl.azplace.model.utility.WebSocketServerInfo;

public interface BackendListener {

    void onConnectionLost(WebSocketServerInfo serverInfo);

}
