package de.abstractolotl.azplace.service;

import de.abstractolotl.azplace.model.board.Canvas;
import de.abstractolotl.azplace.model.board.ColorPalette;
import de.abstractolotl.azplace.model.requests.CanvasRequest;
import de.abstractolotl.azplace.model.requests.PaletteRequest;
import de.abstractolotl.azplace.repositories.CanvasRepo;
import de.abstractolotl.azplace.repositories.PaletteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import redis.clients.jedis.Jedis;

import java.util.Optional;

@Service
public class OperationService {

    @Autowired private Jedis jedis;
    @Autowired private CanvasRepo canvasRepo;
    @Autowired private PaletteRepo paletteRepo;

    public Canvas updateCanvas(Integer id, CanvasRequest canvas) {
        boolean resized = false;
        Canvas currentCanvas = searchCanvas(id);

        if(canvas.getStartDate() > -1L)
            currentCanvas.setStartDate(canvas.getStartDate());

        if(canvas.getDuration() > -1L)
            currentCanvas.setDuration(canvas.getDuration());

        if(canvas.getHeight() > -1) {
            currentCanvas.setHeight(canvas.getHeight());
            resized = true;
        }

        if(canvas.getWidth() > -1) {
            currentCanvas.setWidth(canvas.getWidth());
            resized = true;
        }

        if(canvas.getCooldown() > -1L)
            currentCanvas.setCooldown(canvas.getCooldown());

        if(canvas.getColorPalette() > -1){
            Optional<ColorPalette> palette = paletteRepo.findById(canvas.getColorPalette());

            if(palette.isEmpty())
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Palette does not exists");

            currentCanvas.setColorPalette(palette.get());
        }

        if(resized){
            resize(currentCanvas, currentCanvas.getWidth(), currentCanvas.getHeight());
        }

        return canvasRepo.save(currentCanvas);
    }

    private Canvas searchCanvas(Integer id){
        Optional<Canvas> optionalCurrentCanvas = canvasRepo.findById(id);

        if(optionalCurrentCanvas.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        return optionalCurrentCanvas.get();
    }

    private void resize(Canvas canvas, int height, int width){
        byte[] canvasData = jedis.get(canvas.getRedisKey().getBytes());
        byte[] newCanvasData = createByteArray(height, width);

        for(int i = 0; i < canvasData.length && i < newCanvasData.length; i++){
            newCanvasData[i] = canvasData[i];
        }

        jedis.set(canvas.getRedisKey().getBytes(), newCanvasData);
    }

    public ColorPalette updatePalette(Integer id, PaletteRequest palette){
        Optional<ColorPalette> optionalCurrentPalette = paletteRepo.findById(id);

        if(optionalCurrentPalette.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        ColorPalette currentPalette = optionalCurrentPalette.get();

        if(palette.getHexColors() != null
                && palette.getHexColors() != currentPalette.getHexColors())
            currentPalette.setHexColors(palette.getHexColors());

        return paletteRepo.save(currentPalette);
    }

    public byte[] createByteArray(int height, int width){
        return new byte[height * width];
    }

}
