package de.abstractolotl.azplace.exceptions.board;

import de.abstractolotl.azplace.model.board.Canvas;
import de.abstractolotl.azplace.model.board.ColorPalette;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidColorIndex extends ResponseStatusException {

    public InvalidColorIndex(ColorPalette palette, int colorIndex){
        super(HttpStatus.BAD_REQUEST, "InvalidColorIndex: Color out of bounds. PaletteSize: " + palette.getHexColors().length + ", colorIndex: " + colorIndex);
    }

}
