package de.abstractolotl.azplace.rest.controller;

import de.abstractolotl.azplace.exceptions.CanvasNotFoundException;
import de.abstractolotl.azplace.exceptions.UserCooldownException;
import de.abstractolotl.azplace.model.view.ConfigView;
import de.abstractolotl.azplace.rest.api.BoardAPI;
import de.abstractolotl.azplace.exceptions.UserBannedException;
import de.abstractolotl.azplace.model.board.Canvas;
import de.abstractolotl.azplace.model.logging.PixelOwner;
import de.abstractolotl.azplace.model.requests.PlaceRequest;
import de.abstractolotl.azplace.service.AuthenticationService;
import de.abstractolotl.azplace.service.CooldownService;
import de.abstractolotl.azplace.service.PunishmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import de.abstractolotl.azplace.AzPlaceExceptions.IllegalPixelCoordsException;
import de.abstractolotl.azplace.AzPlaceExceptions.NoUserInSession;
import de.abstractolotl.azplace.AzPlaceExceptions.PixelOutOfBoundsException;
import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.repositories.CanvasRepo;
import de.abstractolotl.azplace.repositories.PixelOwnerRepo;
import redis.clients.jedis.Jedis;

import java.util.HashMap;

@RestController
public class BoardController implements BoardAPI {

    @Autowired
    private Jedis jedis;

    @Autowired
    private CanvasRepo canvasRepo;

    @Autowired
    private PixelOwnerRepo pixelOwnerRepo;

    @Autowired
    private CooldownService cooldownService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private PunishmentService punishmentService;

    @Override
    public void place(int canvasId, PlaceRequest request) {
        User user = authenticationService.getUserFromSession();

        if (user == null) {
            throw new NoUserInSession();
        }

        if(punishmentService.isBanned(user))
            throw new UserBannedException();

        var canvasResp = canvasRepo.findById(canvasId);
        if (canvasResp.isEmpty()) {
            throw new CanvasNotFoundException(canvasId);
        }

        final Canvas canvas = canvasResp.get();

        if(cooldownService.isOnCooldown(user, canvas))
            throw new UserCooldownException();

        if (canvas.getWidth() <= request.getX() || canvas.getHeight() <= request.getY()) {
            throw new PixelOutOfBoundsException(request.getX(), request.getY(), canvas.getWidth(), canvas.getHeight());
        }

        setNewPixelOwner(canvas, request.getX(), request.getY(), user);
        setPixelInBlob(canvas, request.getX(), request.getY(), request.getColor());

        cooldownService.reset(user, canvas);
    }

    @Override
    public byte[] boardData(int canvasId) {
        var canvasRsp = canvasRepo.findById(canvasId);
        if (canvasRsp.isEmpty()) {
            throw new CanvasNotFoundException(canvasId);
        }

        final Canvas canvas = canvasRsp.get();
        return jedis.get(canvas.getRedisKey().getBytes());
    }

    @Override
    public Canvas boardInfo(int canvasId) {
        var canvasRsp = canvasRepo.findById(canvasId);
        if (canvasRsp.isEmpty()) {
            throw new CanvasNotFoundException(canvasId);
        }

        final Canvas canvas = canvasRsp.get();
        return canvas;
    }

    @Override
    public HashMap<String, Long> cooldown(int canvasId) {
        var canvasRsp = canvasRepo.findById(canvasId);
        if (canvasRsp.isEmpty()) {
            throw new CanvasNotFoundException(canvasId);
        }

        Canvas canvas = canvasRsp.get();

        return new HashMap<>(){{
            put("last_pixel", cooldownService.getLastPixelTimestamp(authenticationService.getUserFromSession(), canvas));
        }};
    }

    @Override
    public ConfigView boardConfig(int canvasId) {
        var canvasRsp = canvasRepo.findById(canvasId);
        if (canvasRsp.isEmpty()) {
            throw new CanvasNotFoundException(canvasId);
        }

        return ConfigView.fromCanvas(canvasRsp.get());
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
     * Notes: Doesn't check for correctness of coords
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