package de.abstractolotl.azplace.service.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.abstractolotl.azplace.model.utility.WebSocketServerInfo;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URISyntaxException;

public class WebSocketBackend {
    private final static ObjectMapper JSON = new ObjectMapper();

    private final WebSocketClient client;
    private final WebSocketServerInfo serverInfo;

    private final BackendListener listener;

    public WebSocketBackend(BackendListener listener, WebSocketServerInfo serverInfo) throws ConnectionLostException {
        this.serverInfo = serverInfo;
        this.listener = listener;

        try {
            this.client = initClient();
            auth();
        } catch (URISyntaxException | InterruptedException e) {
            throw new ConnectionLostException(serverInfo, e);
        }
    }

    private void auth() {
        client.send("{ \"method\": \"login\", \"key\": \""+serverInfo.getSecret()+"\" }");
    }

    public void sendMessage(String msg) throws ConnectionLostException {
        try {
            client.send(msg);
        } catch (Exception e) {
            client.close();
            throw new ConnectionLostException(serverInfo, e);
        }
    }

    private WebSocketClient initClient() throws URISyntaxException, InterruptedException {
        var client = new WebSocketClient(serverInfo.getURI()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {

            }

            @Override
            public void onMessage(String s) {
                try {
                    JsonNode node = JSON.readTree(s);
                    if(node.has("error") && node.get("error").asBoolean()) {
                        close();
                        listener.onConnectionLost(serverInfo);
                    }
                } catch (JsonProcessingException e) {
                    close();
                    e.printStackTrace();
                }
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                System.out.println("Connection to Backend Lost: " + s);
                listener.onConnectionLost(serverInfo);
            }

            @Override
            public void onError(Exception e) {
                listener.onConnectionLost(serverInfo);
            }
        };
        client.connectBlocking();
        return client;
    }

    public WebSocketServerInfo getServerInfo() {
        return serverInfo;
    }

}
