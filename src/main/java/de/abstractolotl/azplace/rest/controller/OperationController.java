package de.abstractolotl.azplace.rest.controller;

import de.abstractolotl.azplace.exceptions.board.CanvasNotFoundException;
import de.abstractolotl.azplace.model.requests.ResetRequest;
import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.model.user.UserRoles;
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
import org.springframework.data.redis.core.RedisTemplate;
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

    @Autowired private RedisTemplate<byte[], byte[]> redis;
    @Autowired private OperationService operationService;
    @Autowired private AuthenticationService authService;

    @Override
    public Canvas getCanvas(Integer id) {
        authService.authUserWithRole(UserRoles.ADMIN);

        Optional<Canvas> canvas = canvasRepo.findById(id);

        if(canvas.isEmpty())
            throw new CanvasNotFoundException(id);

        return canvas.get();
    }

    @Override
    public Canvas createCanvas(CanvasRequest canvasRequest) {
        authService.authUserWithRole(UserRoles.ADMIN);

        Optional<ColorPalette> palette = paletteRepo.findById(canvasRequest.getColorPalette());

        if(palette.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Palette does not exists");

        Canvas canvas = canvasRequest.convert(palette.get());
        canvas = canvasRepo.save(canvas);

        redis.opsForValue().set(canvas.getRedisKey().getBytes(), operationService.createByteArray(canvasRequest.getHeight(), canvasRequest.getHeight()));

        return canvas;
    }

    @Override
    public Canvas updateCanvas(Integer id, CanvasRequest canvasRequest) {
        User user = authService.authUser();

        if(!authService.hasRole(user, UserRoles.ADMIN))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        return operationService.updateCanvas(id, canvasRequest);
    }

    @Override
    public ResponseEntity<?> deleteCanvas(Integer id) {
        authService.authUserWithRole(UserRoles.ADMIN);

        Optional<Canvas> canvas = canvasRepo.findById(id);

        if(canvas.isEmpty()) throw new CanvasNotFoundException(id);

        pixelRepo.deleteAll(pixelRepo.findAllByCanvas(canvas.get()));

        redis.delete(canvas.get().getRedisKey().getBytes());
        canvasRepo.delete(canvas.get());

        return ResponseEntity.ok(new HashMap<>(){{
            put("message", "Canvas " + id + " was deleted");
            put("success", true);
        }});
    }

    @Override
    public ColorPalette getPalette(Integer id) {
        authService.authUserWithRole(UserRoles.ADMIN);

        Optional<ColorPalette> palette = paletteRepo.findById(id);

        if(palette.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        return palette.get();
    }

    @Override
    public ColorPalette createPalette(PaletteRequest paletteRequest) {
        authService.authUserWithRole(UserRoles.ADMIN);

        return paletteRepo.save(paletteRequest.convert());
    }

    @Override
    public ColorPalette updatePalette(Integer id, PaletteRequest paletteRequest) {
        authService.authUserWithRole(UserRoles.ADMIN);

        return operationService.updatePalette(id, paletteRequest);
    }

    @Override
    public ResponseEntity<?> deletePalette(Integer id) {
        authService.authUserWithRole(UserRoles.ADMIN);

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

    @Override
    public ResponseEntity<?> resetPixel(Integer id, ResetRequest resetRequest) {
        return null;
    }
}
