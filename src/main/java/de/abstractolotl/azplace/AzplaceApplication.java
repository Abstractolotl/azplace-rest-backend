package de.abstractolotl.azplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AzplaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AzplaceApplication.class, args);
    }

}