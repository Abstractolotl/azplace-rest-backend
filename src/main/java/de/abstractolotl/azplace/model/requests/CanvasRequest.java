package de.abstractolotl.azplace.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.abstractolotl.azplace.model.board.Canvas;
import de.abstractolotl.azplace.model.board.ColorPalette;
import lombok.Data;

@Data
public class CanvasRequest {

    @JsonProperty("color_palette")
    private int colorPalette = -1;

    @JsonProperty("start_date")
    private long startDate = -1L;
    private long duration = -1L;

    private long cooldown = -1L;

    private int width = -1;
    private int height = -1;

    public Canvas convert(ColorPalette colorPalette){
        return Canvas.builder()
                .colorPalette(colorPalette)
                .startDate(startDate).duration(duration)
                .height(height).width(width)
                .cooldown(cooldown)
                .build();
    }

}
