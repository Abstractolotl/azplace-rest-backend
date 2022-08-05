package de.abstractolotl.azplace.model.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.abstractolotl.azplace.model.board.ColorPalette;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaletteRequest {

    @JsonProperty(value = "hex_colors", required = true)
    private String[] hexColors;

    public ColorPalette convert(){
        return ColorPalette.builder()
                .hexColors(hexColors)
                .build();
    }

}
