package com.leadiq.polygonapi;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class PolygonApiApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void contextLoads() {
        assertNotNull(applicationContext, "Application context should not be null");
    }

    @Test
    void cacheManagerIsConfigured() {
        assertNotNull(cacheManager, "Cache manager should be properly configured");
        // You can also test specific cache names if you have them defined
        assertNotNull(cacheManager.getCache("stockPrices"), "stockPrices cache should be available");
    }

    @Test
    void applicationStartup() {
        try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
            PolygonApiApplication.main(new String[]{});
            mocked.verify(() -> SpringApplication.run(PolygonApiApplication.class, new String[]{}));
        }
    }


}
