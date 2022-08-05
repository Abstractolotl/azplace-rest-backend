package de.abstractolotl.azplace.model.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PixelInfoView {

    private String username;

    @JsonProperty("person_id")
    private String personId;

    private long timestamp;

}
