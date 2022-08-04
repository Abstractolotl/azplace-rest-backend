package de.abstractolotl.azplace.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.abstractolotl.azplace.model.requests.PlaceRequest;
import de.abstractolotl.azplace.model.utility.WebSocketServerInfo;
import de.abstractolotl.azplace.model.utility.WebsocketMessage;
import de.abstractolotl.azplace.repositories.WebSocketRepo;
import de.abstractolotl.azplace.service.websocket.BackendListener;
import de.abstractolotl.azplace.service.websocket.ConnectionLostException;
import de.abstractolotl.azplace.service.websocket.WebSocketBackend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WebSocketService implements BackendListener {

    private final static ObjectMapper JSON = new ObjectMapper();

    @Autowired private WebSocketRepo webSocketRepo;

    private Map<String, WebSocketBackend> wsBackends;

    @PostConstruct
    public void init() {
        wsBackends = new HashMap<>();

        var serverInfos = webSocketRepo.findAll();
        for(var info : serverInfos) {
            connectToBackend(info);
        }
    }

    @Scheduled(fixedRate = 60_000, initialDelay = 60_000)
    private void checkForNewWSBackends() {
        var serverInfos = webSocketRepo.findAll();
        for(var info : serverInfos) {
            if(!wsBackends.containsKey(info.getIpAddress()))
                connectToBackend(info);
        }
    }

    private void connectToBackend(WebSocketServerInfo serverInfo) {
        try {
            var backend = new WebSocketBackend(this, serverInfo);
            wsBackends.put(serverInfo.getIpAddress(), backend);
        } catch (ConnectionLostException e) {
            e.printStackTrace();
            onConnectionLost(e.getServerInfo());
        }
    }

    public void broadcastPixel(PlaceRequest request) {
        WebsocketMessage websocketMessage = WebsocketMessage.builder()
                .method("broadcast")
                .data(request)
                .build();
        try {
            String message = JSON.writeValueAsString(websocketMessage);
            for(var backend : wsBackends.values()) {
                backend.sendMessage(message);
            }
        } catch (ConnectionLostException e) {
            e.printStackTrace();
            onConnectionLost(e.getServerInfo());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionLost(WebSocketServerInfo serverInfo) {
        wsBackends.remove(serverInfo.getIpAddress());
        webSocketRepo.delete(serverInfo);
    }
}
