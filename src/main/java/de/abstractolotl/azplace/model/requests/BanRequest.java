package de.abstractolotl.azplace.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BanRequest {

    @NotNull
    @JsonProperty(value = "user_id", required = true)
    public Integer userId;

    @NotNull
    @JsonProperty(required = true)
    public String reason;

    public long time = -1L;

}
