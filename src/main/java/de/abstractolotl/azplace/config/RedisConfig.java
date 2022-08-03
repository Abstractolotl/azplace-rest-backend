package de.abstractolotl.azplace.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redisUrl;
    @Value("${spring.redis.port}")
    private int redisPort;
    @Value("${spring.redis.password}")
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
