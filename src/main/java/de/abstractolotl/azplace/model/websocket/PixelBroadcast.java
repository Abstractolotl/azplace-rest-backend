package de.abstractolotl.azplace.model.websocket;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.abstractolotl.azplace.model.board.Canvas;
import de.abstractolotl.azplace.model.requests.PlaceRequest;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PixelBroadcast {

    @JsonProperty(required = true)
    private int x;

    @JsonProperty(required = true)
    private int y;

    @JsonProperty(value = "color_index", required = true)
    private int colorIndex;

    @JsonProperty(value = "board_id", required = true)
    private int boardId;

    public static PixelBroadcast convertRequest(Canvas canvas, PlaceRequest request){
        return PixelBroadcast.builder()
                .x(request.getX())
                .y(request.getY())
                .colorIndex(request.getColorIndex())
                .boardId(canvas.getId())
                .build();
    }

}
