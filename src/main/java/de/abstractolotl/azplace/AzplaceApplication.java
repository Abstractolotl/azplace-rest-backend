package de.abstractolotl.azplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import redis.clients.jedis.Jedis;

@SpringBootApplication
public class AzplaceApplication {
    @Bean
    public Jedis jedis() {
        Jedis jedis = new Jedis("noucake.ddns.net", 6379);
        return jedis;
    }

    public static void main(String[] args) {
        SpringApplication.run(AzplaceApplication.class, args);
    }

}
