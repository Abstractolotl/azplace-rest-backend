package de.abstractolotl.azplace.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.abstractolotl.azplace.AzPlaceExceptions.CanvasNotFoundExeption;
import de.abstractolotl.azplace.AzPlaceExceptions.IllegalPixelCoordsException;
import de.abstractolotl.azplace.AzPlaceExceptions.NoUserInSession;
import de.abstractolotl.azplace.AzPlaceExceptions.PixelOutOfBoundsException;
import de.abstractolotl.azplace.model.Canvas;
import de.abstractolotl.azplace.model.PixelOwner;
import de.abstractolotl.azplace.model.User;
import de.abstractolotl.azplace.model.requests.PlaceRequest;
import de.abstractolotl.azplace.repositories.CanvasRepo;
import de.abstractolotl.azplace.repositories.PixelOwnerRepo;
import redis.clients.jedis.Jedis;

@RestController
@RequestMapping("/board")
public class BoardController implements BoardAPI {

    @Autowired
    private Jedis          jedis;
    @Autowired
    private CanvasRepo     canvasRepo;
    @Autowired
    private PixelOwnerRepo pixelOwnerRepo;
    @Autowired
    private AuthController authController;

    @Override
    public void place(int canvasId, PlaceRequest request, String sessionKey) {
        authController.isSessionValid(sessionKey);
        checkPixelCords(request.getX(), request.getY());
        final User user = getUserFromSession(sessionKey);
        if (user == null) {
            throw new NoUserInSession();
        }

        var canvasResp = canvasRepo.findById(canvasId);
        if (canvasResp.isEmpty()) {
            throw new CanvasNotFoundExeption(canvasId);
        }

        final Canvas canvas = canvasResp.get();
        if (canvas.getWidth() <= request.getX() || canvas.getHeight() <= request.getY()) {
            throw new PixelOutOfBoundsException(request.getX(), request.getY(), canvas.getWidth(), canvas.getHeight());
        }

        setNewPixelOwner(canvas, request.getX(), request.getY(), getUserFromSession(sessionKey));
        setPixelInBlob(canvas, request.getX(), request.getY(), request.getColor());
    }

    @Override
    public byte[] boardData(int canvasId) {
        var canvasRsp = canvasRepo.findById(canvasId);
        if (canvasRsp.isEmpty()) {
            throw new CanvasNotFoundExeption(canvasId);
        }

        final Canvas canvas = canvasRsp.get();
        return jedis.get(canvas.getRedisKey().getBytes());
    }

    @Override
    public Canvas boardInfo(int canvasId) {
        var canvasRsp = canvasRepo.findById(canvasId);
        if (canvasRsp.isEmpty()) {
            throw new CanvasNotFoundExeption(canvasId);
        }

        final Canvas canvas = canvasRsp.get();
        return canvas;
    }

    private User getUserFromSession(String sessionKey) {
        return authController.getSession(sessionKey).getUser();
    }

    private void setPixelInBlob(Canvas canvas, int x, int y, byte color) {
        //TODO canvas id
        int offset = getBlobOffsetForPixel(canvas.getWidth(), canvas.getHeight(), x, y) * 8;
        jedis.bitfield(canvas.getRedisKey(), "SET", "u8", String.valueOf(offset), String.valueOf(color));
    }

    private void checkPixelCords(int x, int y) {
        if (x < 0 || y < 0) {
            throw new IllegalPixelCoordsException();
        }
    }

    /**
     * Notes: Doesn't check for correctnes of coords
     */
    private PixelOwner createPixelOwner(Canvas canvas, int x, int y) {
        PixelOwner pixel = new PixelOwner();
        pixel.setCanvas(canvas);
        pixel.setX(x);
        pixel.setY(y);
        return pixel;
    }

    private void setNewPixelOwner(Canvas canvas, int x, int y, User user) {
        var        pixelResp = pixelOwnerRepo.findByXAndY(x, y);
        PixelOwner pixel     = pixelResp.orElseGet(() -> createPixelOwner(canvas, x, y));
        pixel.setTimestamp(System.currentTimeMillis());
        pixel.setUser(user);

        pixelOwnerRepo.save(pixel);
    }

    private int getBlobOffsetForPixel(int width, int height, int x, int y) {
        return y * width + x;
    }
}