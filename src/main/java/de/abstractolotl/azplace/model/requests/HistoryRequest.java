package de.abstractolotl.azplace.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HistoryRequest {

    @JsonProperty("start_time")
    private long startTime;
    @JsonProperty("end_time")
    private long endTime;

}
