package de.abstractolotl.azplace.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import de.abstractolotl.azplace.exceptions.board.InvalidColorIndex;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.HashMap;

@Slf4j
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

        checkPlaceRequest(canvas, request);

        setNewPixelOwner(canvas, request.getX(), request.getY(), user);
        setPixelInBlob(canvas, request.getX(), request.getY(), (byte) request.getColorIndex());

        elasticService.logPixel(canvas.getId(), user.getId(), request.getX(), request.getY(), request.getColorIndex());
        cooldownService.reset(user, canvas);
        webSocketService.broadcastPixel(request);
    }

    @Transactional
    public void placePixel(UserBotToken botToken, Canvas canvas, PlaceRequest request){
        try {
            checkBotRateLimit(botToken);
        } catch (IOException ignored) { }

        long lastRequest = 0L;
        if(jedis.get(("bots:" + botToken.getToken()).getBytes()) != null)
            lastRequest = Long.parseLong(new String(jedis.get(("bots:" + botToken.getToken()).getBytes())));

        if(System.currentTimeMillis() < lastRequest + (canvas.getCooldown() * 1.5))
            throw new UserCooldownException();

        checkPlaceRequest(canvas, request);

        setNewPixelOwner(canvas, request.getX(), request.getY(), botToken.getUser());
        setPixelInBlob(canvas, request.getX(), request.getY(), (byte) request.getColorIndex());

        elasticService.logPixel(canvas.getId(), botToken.getUser().getId(),
                request.getX(), request.getY(), request.getColorIndex(),
                true);
        jedis.set(("bots:" + botToken.getToken()).getBytes(), String.valueOf(System.currentTimeMillis()).getBytes());
        webSocketService.broadcastPixel(request);
    }

    private void checkBotRateLimit(UserBotToken botToken) throws IOException {
        JsonMapper jsonMapper = new JsonMapper();

        if(jedis.get(("bots:rates:" + botToken.getToken()).getBytes()) == null){
            HashMap<String, Object> data = new HashMap<>(){{
                put("last_reset", System.currentTimeMillis() + 60_000);
                put("rate", 1);
            }};

            jedis.set(("bots:rates:" + botToken.getToken()).getBytes(), jsonMapper.writeValueAsString(data).getBytes());
            return;
        }

        byte[] rateBytes = jedis.get(("bots:rates:" + botToken.getToken()).getBytes());

        JsonNode jsonNode = jsonMapper.readTree(rateBytes);

        if(System.currentTimeMillis() >= jsonNode.get("last_reset").asLong()){
            HashMap<String, Object> data = new HashMap<>(){{
                put("last_reset", System.currentTimeMillis() + 60_000);
                put("rate", 1);
            }};

            jedis.set(("bots:rates:" + botToken.getToken()).getBytes(), jsonMapper.writeValueAsString(data).getBytes());
            return;
        }

        if(jsonNode.get("rate").asInt() >= botToken.getRateLimit())
            throw new RateLimitException();

        HashMap<String, Object> data = new HashMap<>(){{
            put("last_reset", jsonNode.get("last_reset").asLong());
            put("rate", jsonNode.get("rate").asInt() + 1);
        }};
        jedis.set(("bots:rates:" + botToken.getToken()).getBytes(), jsonMapper.writeValueAsString(data).getBytes());
    }

    private void checkPlaceRequest(Canvas canvas, PlaceRequest request){
        if (canvas.getWidth() <= request.getX() || canvas.getHeight() <= request.getY()) {
            throw new PixelOutOfBoundsException(request.getX(), request.getY(), canvas.getWidth(), canvas.getHeight());
        }

        if(request.getColorIndex() < 0 || request.getColorIndex() >= canvas.getColorPalette().getHexColors().length) {
            throw new InvalidColorIndex(canvas.getColorPalette(), request.getColorIndex());
        }
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
