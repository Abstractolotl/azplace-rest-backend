package de.abstractolotl.azplace.model.utility;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PixelRegion {

    private int minX;
    private int minY;

    private int maxX;
    private int maxY;

}
