package de.abstractolotl.azplace.rest.controller;

import de.abstractolotl.azplace.exceptions.CanvasNotFoundException;
import de.abstractolotl.azplace.model.board.Canvas;
import de.abstractolotl.azplace.model.board.ColorPalette;
import de.abstractolotl.azplace.model.view.PaletteView;
import de.abstractolotl.azplace.model.view.SizeView;
import de.abstractolotl.azplace.model.view.TimespanView;
import de.abstractolotl.azplace.repositories.CanvasRepo;
import de.abstractolotl.azplace.rest.api.SettingsAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class SettingsController implements SettingsAPI {

    @Autowired
    private CanvasRepo canvasRepo;

    @Override
    public PaletteView colors(int canvasId) {
        Optional<Canvas> canvasOptional = canvasRepo.findById(canvasId);

        if(canvasOptional.isEmpty())
            throw new CanvasNotFoundException(canvasId);

        return PaletteView.fromPalette(canvasOptional.get().getColorPalette());
    }

    @Override
    public TimespanView timespan(int canvasId) {
        Optional<Canvas> canvasOptional = canvasRepo.findById(canvasId);

        if(canvasOptional.isEmpty())
            throw new CanvasNotFoundException(canvasId);

        return TimespanView.fromCanvas(canvasOptional.get());
    }

    @Override
    public SizeView size(int canvasId) {
        Optional<Canvas> canvasOptional = canvasRepo.findById(canvasId);

        if(canvasOptional.isEmpty())
            throw new CanvasNotFoundException(canvasId);

        return SizeView.fromCanvas(canvasOptional.get());
    }

}
