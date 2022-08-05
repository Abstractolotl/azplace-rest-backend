package de.abstractolotl.azplace.model.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CooldownView {

    @JsonProperty("last_pixel")
    private long lastPixel;
    private long cooldown;

}
