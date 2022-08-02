package de.abstractolotl.azplace.rest.controller;

import de.abstractolotl.azplace.rest.api.OperationAPI;
import de.abstractolotl.azplace.model.board.Canvas;
import de.abstractolotl.azplace.model.board.ColorPalette;
import de.abstractolotl.azplace.model.requests.CanvasRequest;
import de.abstractolotl.azplace.model.requests.PaletteRequest;
import de.abstractolotl.azplace.repositories.CanvasRepo;
import de.abstractolotl.azplace.repositories.PaletteRepo;
import de.abstractolotl.azplace.repositories.PixelOwnerRepo;
import de.abstractolotl.azplace.service.AuthenticationService;
import de.abstractolotl.azplace.service.OperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Optional;

@RestController
public class OperationController implements OperationAPI {

    @Autowired private CanvasRepo canvasRepo;
    @Autowired private PaletteRepo paletteRepo;
    @Autowired private PixelOwnerRepo pixelRepo;

    @Autowired private Jedis jedis;
    @Autowired private OperationService operationService;
    @Autowired private AuthenticationService authenticationService;

    @Override
    public Canvas getCanvas(Integer id) {
        if(!authenticationService.hasRole("admin"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        Optional<Canvas> canvas = canvasRepo.findById(id);

        if(canvas.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        return canvas.get();
    }

    @Override
    public Canvas createCanvas(CanvasRequest canvasRequest) {
        if(!authenticationService.hasRole("admin"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        Optional<ColorPalette> palette = paletteRepo.findById(canvasRequest.getColorPalette());

        if(palette.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Palette does not exists");

        jedis.set(canvasRequest.getRedisKey().getBytes(),
                operationService.createByteArray(canvasRequest.getHeight(), canvasRequest.getHeight()));

        return canvasRepo.save(canvasRequest.convert(palette.get()));
    }

    @Override
    public Canvas updateCanvas(Integer id, CanvasRequest canvasRequest) {
        if(!authenticationService.hasRole(authenticationService.getUserFromSession(), "admin"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        return operationService.updateCanvas(id, canvasRequest);
    }

    @Override
    public ResponseEntity<?> deleteCanvas(Integer id) {
        if(!authenticationService.hasRole(authenticationService.getUserFromSession(), "admin"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        Optional<Canvas> canvas = canvasRepo.findById(id);

        if(canvas.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        pixelRepo.deleteAll(pixelRepo.findAllByCanvas(canvas.get()));

        jedis.del(canvas.get().getRedisKey());
        canvasRepo.delete(canvas.get());

        return ResponseEntity.ok(new HashMap<>(){{
            put("message", "Canvas " + id + " was deleted");
            put("success", true);
        }});
    }

    @Override
    public ColorPalette getPalette(Integer id) {
        if(!authenticationService.hasRole(authenticationService.getUserFromSession(), "admin"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        Optional<ColorPalette> palette = paletteRepo.findById(id);

        if(palette.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        return palette.get();
    }

    @Override
    public ColorPalette createPalette(PaletteRequest paletteRequest) {
        if(!authenticationService.hasRole(authenticationService.getUserFromSession(), "admin"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        return paletteRepo.save(paletteRequest.convert());
    }

    @Override
    public ColorPalette updatePalette(Integer id, PaletteRequest paletteRequest) {
        if(!authenticationService.hasRole(authenticationService.getUserFromSession(), "admin"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        return operationService.updatePalette(id, paletteRequest);
    }

    @Override
    public ResponseEntity<?> deletePalette(Integer id) {
        if(!authenticationService.hasRole(authenticationService.getUserFromSession(), "admin"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        Optional<ColorPalette> palette = paletteRepo.findById(id);

        if(palette.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        try {
            paletteRepo.delete(palette.get());
        }catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Color palette couldn't be deleted (still in use?)");
        }

        return ResponseEntity.ok(new HashMap<>(){{
            put("message", "Color Palette " + id + " was deleted");
            put("success", true);
        }});
    }
}