package de.abstractolotl.azplace.service;

import de.abstractolotl.azplace.exceptions.board.InvalidColorIndex;
import de.abstractolotl.azplace.exceptions.board.PixelOutOfBoundsException;
import de.abstractolotl.azplace.exceptions.board.UserCooldownException;
import de.abstractolotl.azplace.model.board.Canvas;
import de.abstractolotl.azplace.model.requests.PlaceRequest;
import de.abstractolotl.azplace.model.statistic.PixelOwner;
import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.repositories.PixelOwnerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

@Service @Transactional
public class BoardService {

    @Autowired private PixelOwnerRepo pixelOwnerRepo;
    @Autowired private Jedis jedis;

    @Autowired private CooldownService cooldownService;
    @Autowired private ElasticService elasticService;
    @Autowired private WebSocketService webSocketService;

    @Transactional
    public void placePixel(User user, Canvas canvas, PlaceRequest request){
        if(cooldownService.isOnCooldown(user, canvas))
            throw new UserCooldownException();

        if (canvas.getWidth() <= request.getX() || canvas.getHeight() <= request.getY()) {
            throw new PixelOutOfBoundsException(request.getX(), request.getY(), canvas.getWidth(), canvas.getHeight());
        }

        if(request.getColorIndex() < 0 || request.getColorIndex() >= canvas.getColorPalette().getHexColors().length) {
            throw new InvalidColorIndex(canvas.getColorPalette(), request.getColorIndex());
        }

        setNewPixelOwner(canvas, request.getX(), request.getY(), user);
        setPixelInBlob(canvas, request.getX(), request.getY(), (byte) request.getColorIndex());

        elasticService.logPixel(canvas.getId(), user.getId(), request.getX(), request.getY(), request.getColorIndex());
        cooldownService.reset(user, canvas);
        webSocketService.broadcastPixel(request);
    }

    private void setPixelInBlob(Canvas canvas, int x, int y, byte color) {
        int offset = getBlobOffsetForPixel(canvas.getWidth(), canvas.getHeight(), x, y) * 8;
        jedis.bitfield(canvas.getRedisKey(), "SET", "u8", String.valueOf(offset), String.valueOf(color));
    }

    private PixelOwner createPixelOwner(Canvas canvas, int x, int y) {
        PixelOwner pixel = new PixelOwner();
        pixel.setCanvas(canvas);
        pixel.setX(x);
        pixel.setY(y);
        return pixel;
    }

    private void setNewPixelOwner(Canvas canvas, int x, int y, User user) {
        var        pixelResp = pixelOwnerRepo.findByXAndYAndCanvas(x, y, canvas);
        PixelOwner pixel     = pixelResp.orElseGet(() -> createPixelOwner(canvas, x, y));
        pixel.setTimestamp(System.currentTimeMillis());
        pixel.setUser(user);

        pixelOwnerRepo.save(pixel);
    }

    private int getBlobOffsetForPixel(int width, int height, int x, int y) {
        return y * width + x;
    }

}
