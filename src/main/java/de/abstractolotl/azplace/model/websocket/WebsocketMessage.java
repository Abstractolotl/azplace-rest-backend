package de.abstractolotl.azplace.model.websocket;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebsocketMessage {

    private String method;
    private Object data;

}
