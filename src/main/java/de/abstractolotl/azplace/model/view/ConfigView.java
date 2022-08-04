package de.abstractolotl.azplace.model.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.abstractolotl.azplace.model.board.Canvas;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ConfigView {

    private long cooldown;

    @JsonProperty("hex_colors")
    private String[] hexColors;

    private SizeView size;

    private TimespanView timespan;

    public static ConfigView fromCanvas(Canvas canvas){
        return ConfigView.builder()
                .cooldown(canvas.getCooldown())
                .hexColors(canvas.getColorPalette().getHexColors())
                .size(SizeView.fromCanvas(canvas))
                .timespan(TimespanView.fromCanvas(canvas))
                .build();
    }

}
