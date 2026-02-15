package com.mawa3id;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Mawa3idApplication {

    public static void main(String[] args) {
        SpringApplication.run(Mawa3idApplication.class, args);
    }
}
