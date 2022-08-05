package de.abstractolotl.azplace.rest.controller;

import de.abstractolotl.azplace.exceptions.auth.SessionNotAuthorizedException;
import de.abstractolotl.azplace.exceptions.board.*;
import de.abstractolotl.azplace.exceptions.punishment.UserBannedException;
import de.abstractolotl.azplace.model.statistic.PixelOwner;
import de.abstractolotl.azplace.model.view.ConfigView;
import de.abstractolotl.azplace.model.view.CooldownView;
import de.abstractolotl.azplace.model.view.PixelInfoView;
import de.abstractolotl.azplace.rest.api.BoardAPI;
import de.abstractolotl.azplace.model.board.Canvas;
import de.abstractolotl.azplace.service.*;
import de.abstractolotl.azplace.model.requests.PlaceRequest;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.codec.digest.Sha2Crypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.repositories.CanvasRepo;
import de.abstractolotl.azplace.repositories.PixelOwnerRepo;
import redis.clients.jedis.Jedis;

import java.util.Optional;

@RestController
public class BoardController implements BoardAPI {

    @Autowired private Jedis jedis;

    @Autowired private CanvasRepo canvasRepo;
    @Autowired private PixelOwnerRepo pixelOwnerRepo;

    @Autowired private AuthenticationService authService;
    @Autowired private PunishmentService punishmentService;
    @Autowired private CooldownService cooldownService;
    @Autowired private ElasticService elasticService;
    @Autowired private WebSocketService webSocketService;
    
    @Override
    public void place(int canvasId, PlaceRequest request) {
        User user = authService.authUser();

        if (punishmentService.isBanned(user)) throw new UserBannedException();

        var canvasResp = canvasRepo.findById(canvasId);
        if (canvasResp.isEmpty()) throw new CanvasNotFoundException(canvasId);

        final Canvas canvas = canvasResp.get();

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

    @Override
    public PixelInfoView pixel(int canvasId, int x, int y) {
        User user = null;
        try {
            user = authService.authUser();
        }catch (SessionNotAuthorizedException ignored){ }

        var canvasResp = canvasRepo.findById(canvasId);
        if (canvasResp.isEmpty()) throw new CanvasNotFoundException(canvasId);

        Canvas canvas = canvasResp.get();
        Optional<PixelOwner> optionalPixelOwner = pixelOwnerRepo.findByXAndYAndCanvas(x, y, canvas);

        if(optionalPixelOwner.isEmpty())
            throw new PixelOwnerNotFoundException(x, y, canvasId);

        PixelOwner pixelOwner = optionalPixelOwner.get();
        String username = "anonymous";
        if(!authService.hasRole(pixelOwner.getUser(), "anonymous") ||
                (user != null && authService.hasRole(user, "admin"))) {
            username = pixelOwner.getUser().getFullName();
        }

        return PixelInfoView.builder()
                .username(username)
                .timestamp(pixelOwner.getTimestamp())
                .build();
    }

    @Override
    public byte[] boardData(int canvasId) {
        var canvasRsp = canvasRepo.findById(canvasId);
        if (canvasRsp.isEmpty()) throw new CanvasNotFoundException(canvasId);

        final Canvas canvas = canvasRsp.get();
        return jedis.get(canvas.getRedisKey().getBytes());
    }

    @Override
    public CooldownView cooldown(int canvasId) {
        User user = authService.authUser();

        var canvasRsp = canvasRepo.findById(canvasId);
        if (canvasRsp.isEmpty()) throw new CanvasNotFoundException(canvasId);

        Canvas canvas = canvasRsp.get();

        long lastPixel = cooldownService.getLastPixelTimestamp(user, canvas);
        long cooldown = (lastPixel + canvas.getCooldown()) - System.currentTimeMillis();

        return CooldownView.builder()
                .lastPixel(lastPixel)
                .cooldown(Math.max(0L, cooldown))
                .build();
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
        int offset = getBlobOffsetForPixel(canvas.getWidth(), canvas.getHeight(), x, y) * 8;
        jedis.bitfield(canvas.getRedisKey(), "SET", "u8", String.valueOf(offset), String.valueOf(color));
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