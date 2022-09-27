package de.abstractolotl.azplace.model.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaceRequest {

    @JsonProperty(required = true)
    private int x;

    @JsonProperty(required = true)
    private int y;

    @JsonProperty(value = "color_index", required = true)
    private int colorIndex;

}
