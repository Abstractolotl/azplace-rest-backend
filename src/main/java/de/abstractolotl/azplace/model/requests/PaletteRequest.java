package de.abstractolotl.azplace.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.abstractolotl.azplace.model.canvas.ColorPalette;
import lombok.Data;

@Data
public class PaletteRequest {

    @JsonProperty("hex_colors")
    private String[] hexColors;

    public ColorPalette convert(){
        return ColorPalette.builder()
                .hexColors(hexColors)
                .build();
    }

}
