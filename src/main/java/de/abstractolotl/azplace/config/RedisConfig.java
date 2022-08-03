package de.abstractolotl.azplace.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
public class RedisConfig {

    @Value("${app.redis.url}")
    private String redisUrl;
    @Value("${app.redis.port}")
    private int    redisPort;
    @Value("${app.redis.password}")
    private String redisPassword;

    @Bean
    public Jedis jedis() {
        if (redisPassword.isBlank()) {
            return new Jedis(redisUrl, redisPort);
        }
        final Jedis jedis = new Jedis(redisUrl, redisPort);
        jedis.auth(redisPassword);
        return jedis;
    }

}
