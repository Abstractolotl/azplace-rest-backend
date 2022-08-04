package de.abstractolotl.azplace.model.utility;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebsocketMessage {

    private String method;
    private Object data;

}
