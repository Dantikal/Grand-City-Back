package com.grandcity.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GrandCityBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrandCityBackendApplication.class, args);
    }
}
