package de.abstractolotl.azplace.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.abstractolotl.azplace.model.requests.PlaceRequest;
import de.abstractolotl.azplace.model.utility.WebSocketServerInfo;
import de.abstractolotl.azplace.repositories.WebSocketRepo;
import de.abstractolotl.azplace.service.websocket.BackendListener;
import de.abstractolotl.azplace.service.websocket.ConnectionLostException;
import de.abstractolotl.azplace.service.websocket.WebSocketBackend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Service
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
        System.out.println("Checking for new WS Backends");
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
        try {
            String message = "{ \"method\": \"broadcast\", \"data\": \""+JSON.writeValueAsString(request)+"\"}";
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
