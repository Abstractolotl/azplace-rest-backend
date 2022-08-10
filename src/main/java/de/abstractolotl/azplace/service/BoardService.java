package de.abstractolotl.azplace.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import de.abstractolotl.azplace.exceptions.board.InvalidColorIndex;
import de.abstractolotl.azplace.exceptions.board.OutsideTimespanException;
import de.abstractolotl.azplace.exceptions.board.PixelOutOfBoundsException;
import de.abstractolotl.azplace.exceptions.board.UserCooldownException;
import de.abstractolotl.azplace.exceptions.bot.RateLimitException;
import de.abstractolotl.azplace.model.board.Canvas;
import de.abstractolotl.azplace.model.requests.PlaceRequest;
import de.abstractolotl.azplace.model.statistic.PixelOwner;
import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.model.user.UserBotToken;
import de.abstractolotl.azplace.repositories.PixelOwnerRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;

@Slf4j
@Service @Transactional
public class BoardService {

    @Autowired private PixelOwnerRepo pixelOwnerRepo;
    @Autowired private RedisTemplate<byte[], byte[]> redis;

    @Autowired private CooldownService cooldownService;
    @Autowired private ElasticService elasticService;
    @Autowired private WebSocketService webSocketService;
    @Autowired private BotService botService;

    @Transactional
    public void placePixel(User user, Canvas canvas, PlaceRequest request){
        if(cooldownService.isOnCooldown(user, canvas))
            throw new UserCooldownException();

        updatePixel(canvas, request, user);
    }

    @Transactional
    public void placePixel(UserBotToken botToken, Canvas canvas, PlaceRequest request){
        botService.checkAcceptance(canvas, botToken);

        updatePixel(canvas, request, botToken.getUser(), true);
        redis.opsForValue().set(("bots:" + botToken.getToken()).getBytes(), String.valueOf(System.currentTimeMillis()).getBytes());
    }

    private void updatePixel(Canvas canvas, PlaceRequest request, User user){
        updatePixel(canvas, request, user, false);
        cooldownService.reset(user, canvas);
    }

    private void updatePixel(Canvas canvas, PlaceRequest request, User user, boolean bot){
        checkPlaceRequest(canvas, request);

        setNewPixelOwner(canvas, request.getX(), request.getY(), user);
        setPixelInBlob(canvas, request.getX(), request.getY(), (byte) request.getColorIndex());

        webSocketService.broadcastPixel(canvas, request);
        elasticService.logPixel(canvas.getId(), user.getId(),
                request.getX(), request.getY(), request.getColorIndex(),
                bot);
    }

    private void checkPlaceRequest(Canvas canvas, PlaceRequest request){
        if(System.currentTimeMillis() < canvas.getStartDate() || System.currentTimeMillis() > (canvas.getStartDate() + canvas.getDuration()))
            throw new OutsideTimespanException();

        if (canvas.getWidth() <= request.getX() || canvas.getHeight() <= request.getY())
            throw new PixelOutOfBoundsException(request.getX(), request.getY(), canvas.getWidth(), canvas.getHeight());

        if(request.getColorIndex() < 0 || request.getColorIndex() >= canvas.getColorPalette().getHexColors().length)
            throw new InvalidColorIndex(canvas.getColorPalette(), request.getColorIndex());
    }

    private void setPixelInBlob(Canvas canvas, int x, int y, byte color) {
        int offset = getBlobOffsetForPixel(canvas.getWidth(), canvas.getHeight(), x, y) * 8;
        var command = BitFieldSubCommands.BitFieldSet.create(BitFieldSubCommands.BitFieldType.UINT_8, BitFieldSubCommands.Offset.offset(offset), color);
        redis.opsForValue().bitField(canvas.getRedisKey().getBytes(), BitFieldSubCommands.create(command));
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
