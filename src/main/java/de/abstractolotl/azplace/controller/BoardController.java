package de.abstractolotl.azplace.controller;

import de.abstractolotl.azplace.model.Canvas;
import de.abstractolotl.azplace.model.PixelOwner;
import de.abstractolotl.azplace.model.User;
import de.abstractolotl.azplace.model.requests.PlaceRequest;
import de.abstractolotl.azplace.repositorys.CanvasRepo;
import de.abstractolotl.azplace.repositorys.PixelOwnerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import de.abstractolotl.azplace.AzPlaceExceptions.*;
import redis.clients.jedis.Jedis;

@RestController
public class BoardController {

    @Autowired private Jedis jedis;
    @Autowired private CanvasRepo canvasRepo;
    @Autowired private PixelOwnerRepo pixelOwnerRepo;

    @PostMapping("/place")
    private void place(@RequestBody PlaceRequest request) {
        checkPixelCords(request.getX(), request.getY());
        User user = getUserFromSession();
        if(user == null) throw new RuntimeException("No user in Session"); //TODO

        var canvasResp = canvasRepo.findById(request.getCanvasId());
        if(canvasResp.isEmpty()) throw  new CanvasNotFoundExeption(request.getCanvasId());

        Canvas canvas = canvasResp.get();
        if(canvas.getWidth() <= request.getX() || canvas.getHeight() <= request.getY())
            throw new PixelOutOfBoundsException(request.getX(), request.getY(), canvas.getWidth(), canvas.getHeight());

        setNewPixelOwner(canvas, request.getX(), request.getY(), getUserFromSession());
        setPixelInBlob(canvas, request.getX(), request.getY(), request.getColor());
    }

    @GetMapping("/board/{canvasId}")
    public byte[] boardData(@PathVariable int canvasId) {
        var canvasRsp = canvasRepo.findById(canvasId);
        if(canvasRsp.isEmpty()) throw new CanvasNotFoundExeption(canvasId);

        Canvas canvas = canvasRsp.get();
        return jedis.get(canvas.getRedisKey().getBytes());
    }

    private User getUserFromSession() {
        //TODO
        User user = new User();
        user.setId(999);
        user.setFullname("Bobb Bobert");
        user.setInsideNetIdentifier("nope");
        user.setTimestampRegistered(System.currentTimeMillis());
        user.setTimestampLastPixel(System.currentTimeMillis());
        return user;
    }
    private void setPixelInBlob(Canvas canvas, int x, int y, byte color) {
        //TODO canvas id
        int offset = getBlobOffsetForPixel(canvas.getWidth(), canvas.getHeight(), x, y) * 8;
        jedis.bitfield(canvas.getRedisKey(), "SET", "u8", String.valueOf(offset), String.valueOf(color));
    }

    private void checkPixelCords(int x, int y){
        if(x < 0 || y < 0) throw new IllegalPixelCoordsException();
    }

    /** Notes: Doesn't check for correctnes of coords */
    private PixelOwner createPixelOwner(Canvas canvas, int x, int y) {
        PixelOwner pixel = new PixelOwner();
        pixel.setCanvas(canvas);
        pixel.setX(x);
        pixel.setY(y);
        return pixel;
    }

    private void setNewPixelOwner(Canvas canvas, int x, int y, User user) {
        var pixelResp = pixelOwnerRepo.findByXAndY(x, y);
        PixelOwner pixel = pixelResp.orElseGet(() -> createPixelOwner(canvas, x, y));
        pixel.setTimestamp(System.currentTimeMillis());
        pixel.setUser(user);
        pixelOwnerRepo.save(pixel);
    }

    private int getBlobOffsetForPixel(int width, int height, int x, int y) {
        return y * width + x;
    }

}
