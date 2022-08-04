package de.abstractolotl.azplace.model.websocket;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebsocketAuth {

    @Builder.Default
    private String method = "login";
    private String key;

}
