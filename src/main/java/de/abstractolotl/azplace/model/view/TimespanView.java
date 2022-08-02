package de.abstractolotl.azplace.model.view;

import de.abstractolotl.azplace.model.board.Canvas;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TimespanView {

    public long startDate;
    public long duration;

    public long remainingTime;
    public boolean ended = false;

    public static TimespanView fromCanvas(Canvas canvas){
        long remainingTime = (canvas.getStartDate() + canvas.getDuration()) - System.currentTimeMillis();
        return TimespanView.builder()
                .startDate(canvas.getStartDate())
                .duration(canvas.getDuration())
                .remainingTime(remainingTime)
                .ended(remainingTime > 0)
                .build();
    }

}