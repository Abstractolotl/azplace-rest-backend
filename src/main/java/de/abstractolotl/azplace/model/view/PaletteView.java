package de.abstractolotl.azplace.model.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.abstractolotl.azplace.model.board.ColorPalette;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PaletteView {

    @JsonProperty("hex_colors")
    private String[] hexColors;

    public static PaletteView fromPalette(ColorPalette colorPalette){
        return PaletteView.builder()
                .hexColors(colorPalette.getHexColors())
                .build();
    }

}
