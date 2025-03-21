package com.leadiq.polygonapi;

import com.leadiq.polygonapi.config.OpenApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EnableCaching
@EntityScan("com.leadiq.polygonapi.entity")
@EnableConfigurationProperties(OpenApiProperties.class)
public class PolygonApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PolygonApiApplication.class, args);
    }

}
