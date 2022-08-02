package de.abstractolotl.azplace.service.websocket;

import de.abstractolotl.azplace.model.utility.WebSocketServerInfo;

public interface BackendListener {

    void onConnectionLost(WebSocketServerInfo serverInfo);

}
