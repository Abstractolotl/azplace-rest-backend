package de.abstractolotl.azplace.model.history;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class HistoryInfo {

    @JsonProperty("start_time")
    private long startTime;
    @JsonProperty("end_time")
    private long endTime;

    @JsonProperty("pixel_count")
    private long pixelCount;

}
