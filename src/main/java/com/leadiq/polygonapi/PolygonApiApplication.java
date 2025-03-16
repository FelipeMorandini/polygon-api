package com.leadiq.polygonapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PolygonApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PolygonApiApplication.class, args);
    }

}
