package de.abstractolotl.azplace;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import redis.clients.jedis.Jedis;

@SpringBootApplication
public class AzplaceApplication {

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

    public static void main(String[] args) {
        SpringApplication.run(AzplaceApplication.class, args);
    }

}