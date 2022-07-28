package de.abstractolotl.azplace.controller;

import de.abstractolotl.azplace.model.Canvas;
import de.abstractolotl.azplace.model.PixelOwner;
import de.abstractolotl.azplace.model.User;
import de.abstractolotl.azplace.model.requests.PlaceRequest;
import de.abstractolotl.azplace.repositorys.CanvasRepo;
import de.abstractolotl.azplace.repositorys.PixelOwnerRepo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import de.abstractolotl.azplace.AzPlaceExceptions.*;
import redis.clients.jedis.Jedis;

@RestController
@RequestMapping("/api/board")
public class BoardController {

    @Autowired private Jedis jedis;
    @Autowired private CanvasRepo canvasRepo;
    @Autowired private PixelOwnerRepo pixelOwnerRepo;

    @Operation(
            summary = "Place a Pixel",
            description = "Placed a pixel on the specified Board." +
                    "This endpoint is ready for multi board support." +
                    "for now just use the canasId as 0"
    )
    @PostMapping("{canvasId}/place")
    private void place(@PathVariable int canvasId, @RequestBody PlaceRequest request) {
        checkPixelCords(request.getX(), request.getY());
        final User user = getUserFromSession();
        if(user == null) throw new NoUserInSession();

        var canvasResp = canvasRepo.findById(canvasId);
        if(canvasResp.isEmpty()) throw  new CanvasNotFoundExeption(canvasId);

        final Canvas canvas = canvasResp.get();
        if(canvas.getWidth() <= request.getX() || canvas.getHeight() <= request.getY())
            throw new PixelOutOfBoundsException(request.getX(), request.getY(), canvas.getWidth(), canvas.getHeight());

        setNewPixelOwner(canvas, request.getX(), request.getY(), getUserFromSession());
        setPixelInBlob(canvas, request.getX(), request.getY(), request.getColor());
    }

    @Operation(
            summary = "Board Data",
            description = "Returns the raw binary data of the board. \n" +
                    "See the outdated presentation for further information: \n" +
                    "https://discord.com/channels/@me/758720519804682248/1001125420055400589"
    )
    @GetMapping("/{canvasId}/data")
    public byte[] boardData(@PathVariable int canvasId) {
        var canvasRsp = canvasRepo.findById(canvasId);
        if(canvasRsp.isEmpty()) throw new CanvasNotFoundExeption(canvasId);

        final Canvas canvas = canvasRsp.get();
        return jedis.get(canvas.getRedisKey().getBytes());
    }

    @Operation(
            summary = "Board Info",
            description = "Returns the meta information about a Board."
    )
    @GetMapping("/{canvasId}/info")
    public Canvas boardInfo(@PathVariable int canvasId) {
        var canvasRsp = canvasRepo.findById(canvasId);
        if(canvasRsp.isEmpty()) throw new CanvasNotFoundExeption(canvasId);

        final Canvas canvas = canvasRsp.get();
        return canvas;
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
