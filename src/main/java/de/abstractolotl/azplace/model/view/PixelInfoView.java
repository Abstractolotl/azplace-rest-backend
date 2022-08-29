package de.abstractolotl.azplace.model.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PixelInfoView {

    private String username;

    @JsonProperty("user_id")
    private int userId;

    @JsonProperty("person_id")
    private String personId;

    private long timestamp;

}
