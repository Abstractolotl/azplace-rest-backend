package de.abstractolotl.azplace.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BanRequest {

    @JsonProperty("user_id")
    public Integer userId;

    public String reason;
    public long time = -1L;

}
