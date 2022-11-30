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

    /*
     Timespan in which pixels should be reset (in seconds).
     Is only used if resetPixels is true.
     Default is 1 hour.
     */
    @JsonProperty(value = "reset_timespan")
    public long resetTimespan = 86400L;

}
