package de.abstractolotl.azplace.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PlaceRequest {

    private int x;
    private int y;

    @JsonProperty("color_index")
    private int colorIndex;

}
