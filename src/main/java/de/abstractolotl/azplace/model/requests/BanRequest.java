package de.abstractolotl.azplace.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BanRequest {

    @JsonProperty("user_id")
    public int userId;

    public String reason;
    public long time = -1L;

    @JsonProperty(value = "reset_pixels")
    public boolean resetPixels = false;

}
