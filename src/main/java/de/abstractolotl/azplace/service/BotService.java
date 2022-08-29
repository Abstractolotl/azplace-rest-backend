package de.abstractolotl.azplace.service;

import de.abstractolotl.azplace.exceptions.board.UserCooldownException;
import de.abstractolotl.azplace.exceptions.bot.RateLimitException;
import de.abstractolotl.azplace.model.board.Canvas;
import de.abstractolotl.azplace.model.user.UserBotToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Service
public class BotService {

    @Value("${bot.rate-timeout:15}")
    private int rateTimeout;

    @Autowired private RedisTemplate<byte[], byte[]> redis;

    public void checkAcceptance(Canvas canvas, UserBotToken userBotToken){
        checkRateLimit(userBotToken);
        checkCooldown(canvas, userBotToken);
    }

    private void checkCooldown(Canvas canvas, UserBotToken botToken){
        long lastRequest = 0L;
        if(redis.opsForValue().get(("bots:" + botToken.getToken()).getBytes()) != null)
            lastRequest = Long.parseLong(new String(redis.opsForValue().get(("bots:" + botToken.getToken()).getBytes())));

        if(System.currentTimeMillis() < lastRequest + (canvas.getCooldown() * 2))
            throw new UserCooldownException();
    }

    private void checkRateLimit(UserBotToken botToken){
        if(redis.opsForValue().get(("bots:rates:" + botToken.getToken()).getBytes()) == null){
            int rate = 1;
            redis.opsForValue().set(("bots:rates:" + botToken.getToken()).getBytes(), String.valueOf(rate).getBytes(), Duration.of(rateTimeout, ChronoUnit.MINUTES));
            return;
        }

        byte[] rateData = redis.opsForValue().get(("bots:rates:" + botToken.getToken()).getBytes());
        String rateDataString = new String(rateData);
        int rate = Integer.parseInt(rateDataString);

        if(rate >= botToken.getRateLimit())
            throw new RateLimitException();

        redis.opsForValue().increment(("bots:rates:" + botToken.getToken()).getBytes());
    }

}
