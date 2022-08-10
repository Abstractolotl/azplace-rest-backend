package de.abstractolotl.azplace.model.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.abstractolotl.azplace.model.board.Canvas;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TimespanView {

    @JsonProperty("start_date")
    public long startDate;
    public long duration;

    public boolean started;
    public boolean ended;

    @JsonProperty("remaining_time")
    public long remainingTime;

    public static TimespanView fromCanvas(Canvas canvas){
        long remainingTime = canvas.getStartDate() - System.currentTimeMillis();
        if(System.currentTimeMillis() >= canvas.getStartDate())
            remainingTime = (canvas.getStartDate() + canvas.getDuration()) - System.currentTimeMillis();

        return TimespanView.builder()
                .startDate(canvas.getStartDate())
                .duration(canvas.getDuration())
                .remainingTime(Math.max(remainingTime, 0L))
                .started(System.currentTimeMillis() >= canvas.getStartDate())
                .ended(Math.max(remainingTime, 0L) == 0)
                .build();
    }

}
