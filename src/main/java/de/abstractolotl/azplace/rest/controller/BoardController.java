package de.abstractolotl.azplace.rest.controller;

import de.abstractolotl.azplace.exceptions.auth.SessionNotAuthorizedException;
import de.abstractolotl.azplace.exceptions.board.*;
import de.abstractolotl.azplace.exceptions.punishment.UserBannedException;
import de.abstractolotl.azplace.model.statistic.PixelOwner;
import de.abstractolotl.azplace.model.user.UserRoles;
import de.abstractolotl.azplace.model.view.ConfigView;
import de.abstractolotl.azplace.model.view.CooldownView;
import de.abstractolotl.azplace.model.view.PixelInfoView;
import de.abstractolotl.azplace.rest.api.BoardAPI;
import de.abstractolotl.azplace.model.board.Canvas;
import de.abstractolotl.azplace.service.*;
import de.abstractolotl.azplace.model.requests.PlaceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RestController;

import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.repositories.CanvasRepo;
import de.abstractolotl.azplace.repositories.PixelOwnerRepo;

import java.util.Optional;

@RestController
public class BoardController implements BoardAPI {

    @Autowired private RedisTemplate<byte[], byte[]> redis;
    @Autowired private CanvasRepo canvasRepo;
    @Autowired private PixelOwnerRepo pixelOwnerRepo;

    @Autowired private AuthenticationService authService;
    @Autowired private PunishmentService punishmentService;
    @Autowired private CooldownService cooldownService;
    @Autowired private BoardService boardService;

    @Override
    public void place(int canvasId, PlaceRequest request) {
        User user = authService.authUser();

        if (punishmentService.isBanned(user)) throw new UserBannedException();

        var canvasResp = canvasRepo.findById(canvasId);
        if (canvasResp.isEmpty()) throw new CanvasNotFoundException(canvasId);

        final Canvas canvas = canvasResp.get();

        boardService.placePixel(user, canvas, request);
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

        if(optionalPixelOwner.isEmpty()){
            return PixelInfoView.builder()
                    .userId(-1)
                    .personId("0")
                    .username("unknown")
                    .timestamp(0)
                    .build();
        }

        PixelOwner pixelOwner = optionalPixelOwner.get();
        String username = "anonymous";
        if(!authService.hasRole(pixelOwner.getUser(), UserRoles.ANONYMOUS) ||
                (user != null && authService.hasRole(user, UserRoles.ADMIN))) {
            username = pixelOwner.getUser().getFullName();
        }

        String personId = "0";
        if(!authService.hasRole(pixelOwner.getUser(), UserRoles.ANONYMOUS) ||
                (user != null && authService.hasRole(user, UserRoles.ADMIN))) {
            personId = pixelOwner.getUser().getInsideNetIdentifier();
        }

        int userId = -1;
        if(!authService.hasRole(pixelOwner.getUser(), UserRoles.ANONYMOUS) ||
                (user != null && authService.hasRole(user, UserRoles.ADMIN))) {
            userId = pixelOwner.getUser().getId();
        }

        return PixelInfoView.builder()
                .userId(userId)
                .personId(personId)
                .username(username)
                .timestamp(pixelOwner.getTimestamp())
                .build();
    }

    @Override
    public byte[] boardData(int canvasId) {
        var canvasRsp = canvasRepo.findById(canvasId);
        if (canvasRsp.isEmpty()) throw new CanvasNotFoundException(canvasId);

        final Canvas canvas = canvasRsp.get();

        return redis.opsForValue().get(canvas.getRedisKey().getBytes());
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

}