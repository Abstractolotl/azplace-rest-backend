package de.abstractolotl.azplace.controller;

import de.abstractolotl.azplace.endpoints.OperationAPI;
import de.abstractolotl.azplace.model.Canvas;
import de.abstractolotl.azplace.model.ColorPalette;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OperationController implements OperationAPI {

    @Override
    public Canvas getCanvas(Integer id) {
        return null;
    }

    @Override
    public Canvas createCanvas(Canvas canvas) {
        return null;
    }

    @Override
    public Canvas updateCanvas(Integer id, Canvas canvas) {
        return null;
    }

    @Override
    public ResponseEntity<?> deleteCanvas(Integer id) {
        return null;
    }

    @Override
    public ColorPalette getPalette(Integer id) {
        return null;
    }

    @Override
    public ColorPalette createPalette(ColorPalette canvas) {
        return null;
    }

    @Override
    public ColorPalette updatePalette(Integer id, ColorPalette canvas) {
        return null;
    }

    @Override
    public ResponseEntity<?> deletePalette(Integer id) {
        return null;
    }
}
