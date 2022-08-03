package de.abstractolotl.azplace.model.view;

import de.abstractolotl.azplace.model.board.Canvas;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SizeView {

    private int width;
    private int height;

    public static SizeView fromCanvas(Canvas canvas){
        return SizeView.builder()
                .width(canvas.getWidth())
                .height(canvas.getHeight())
                .build();
    }

}
