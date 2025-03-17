package com.leadiq.polygonapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")  // This is crucial to load the test properties
class PolygonApiApplicationTests {

    @Test
    void contextLoads() {
    }
}
