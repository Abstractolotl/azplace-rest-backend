package de.abstractolotl.azplace.service;

import de.abstractolotl.azplace.model.board.Canvas;
import de.abstractolotl.azplace.model.board.ColorPalette;
import de.abstractolotl.azplace.model.requests.CanvasRequest;
import de.abstractolotl.azplace.model.requests.PaletteRequest;
import de.abstractolotl.azplace.repositories.CanvasRepo;
import de.abstractolotl.azplace.repositories.PaletteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class OperationService {

    @Autowired private CanvasRepo canvasRepo;
    @Autowired private PaletteRepo paletteRepo;
    @Autowired private RedisTemplate<byte[], byte[]> redis;

    public Canvas updateCanvas(Integer id, CanvasRequest canvas) {
        boolean resized = false;
        Canvas currentCanvas = searchCanvas(id);

        int currentWith = currentCanvas.getWidth();
        int currentHeight = currentCanvas.getHeight();

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
            resize(currentCanvas, currentCanvas.getHeight(), currentCanvas.getWidth(),
                    currentHeight, currentWith);
        }

        return canvasRepo.save(currentCanvas);
    }

    private Canvas searchCanvas(Integer id){
        Optional<Canvas> optionalCurrentCanvas = canvasRepo.findById(id);

        if(optionalCurrentCanvas.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        return optionalCurrentCanvas.get();
    }

    private void resize(Canvas canvas, int height, int width, int currentHeight, int currentWidth){
        byte[] canvasData = redis.opsForValue().get(canvas.getRedisKey().getBytes());
        byte[] newCanvasData = createByteArray(height, width);

        for(int y = 0; y < height && y < currentHeight; y++){
            for(int x = 0; x < width && x < currentWidth; x++){
                newCanvasData[x + (y * width)] = canvasData[x + (y * currentWidth)];
            }
        }

        redis.opsForValue().set(canvas.getRedisKey().getBytes(), newCanvasData);
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
