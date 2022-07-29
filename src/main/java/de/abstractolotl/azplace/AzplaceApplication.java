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
    private int redisPort;

    @Bean
    public Jedis jedis() {
        Jedis jedis = new Jedis(redisUrl, redisPort);
        return jedis;
    }

    public static void main(String[] args) {
        SpringApplication.run(AzplaceApplication.class, args);
    }

}
