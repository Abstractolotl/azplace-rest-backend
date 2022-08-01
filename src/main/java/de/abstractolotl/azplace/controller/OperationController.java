package de.abstractolotl.azplace.controller;

import de.abstractolotl.azplace.api.OperationAPI;
import de.abstractolotl.azplace.model.canvas.Canvas;
import de.abstractolotl.azplace.model.canvas.ColorPalette;
import de.abstractolotl.azplace.model.requests.CanvasRequest;
import de.abstractolotl.azplace.model.requests.PaletteRequest;
import de.abstractolotl.azplace.repositories.CanvasRepo;
import de.abstractolotl.azplace.repositories.PaletteRepo;
import de.abstractolotl.azplace.repositories.PixelOwnerRepo;
import de.abstractolotl.azplace.service.OperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Optional;

@RestController
public class OperationController implements OperationAPI {

    @Autowired private CanvasRepo canvasRepo;
    @Autowired private PaletteRepo paletteRepo;
    @Autowired private PixelOwnerRepo pixelRepo;

    @Autowired
    private OperationService operationService;

    @Override
    public Canvas getCanvas(Integer id) {
        Optional<Canvas> canvas = canvasRepo.findById(id);

        if(canvas.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        return canvas.get();
    }

    @Override
    public Canvas createCanvas(CanvasRequest canvas) {
        Optional<ColorPalette> palette = paletteRepo.findById(canvas.getColorPalette());

        if(palette.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Palette does not exists");

        return canvasRepo.save(canvas.convert(palette.get()));
    }

    @Override
    public Canvas updateCanvas(Integer id, CanvasRequest canvas) {
        return operationService.updateCanvas(id, canvas);
    }

    @Override
    public ResponseEntity<?> deleteCanvas(Integer id) {
        Optional<Canvas> canvas = canvasRepo.findById(id);

        if(canvas.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        pixelRepo.deleteAll(pixelRepo.findAllByCanvas(canvas.get()));

        canvasRepo.delete(canvas.get());

        return ResponseEntity.ok(new HashMap<>(){{
            put("message", "Canvas " + id + " was deleted");
            put("success", true);
        }});
    }

    @Override
    public ColorPalette getPalette(Integer id) {
        Optional<ColorPalette> palette = paletteRepo.findById(id);

        if(palette.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        return palette.get();
    }

    @Override
    public ColorPalette createPalette(PaletteRequest canvas) {
        return paletteRepo.save(canvas.convert());
    }

    @Override
    public ColorPalette updatePalette(Integer id, PaletteRequest palette) {
        return operationService.updatePalette(id, palette);
    }

    @Override
    public ResponseEntity<?> deletePalette(Integer id) {
        Optional<ColorPalette> palette = paletteRepo.findById(id);

        if(palette.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        if(canvasRepo.findAllByColorPalette(palette.get()).size() > 0)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "The Color palette is still in use and cannot be deleted");

        paletteRepo.delete(palette.get());

        return ResponseEntity.ok(new HashMap<>(){{
            put("message", "Color Palette " + id + " was deleted");
            put("success", true);
        }});
    }
}
