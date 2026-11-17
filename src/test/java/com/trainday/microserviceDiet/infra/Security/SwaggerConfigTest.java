package com.trainday.microserviceDiet.infra.Security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SwaggerConfigTest {
    @Test
    void shouldCreateSwaggerConfig() {
        SwaggerConfig swaggerConfig = new SwaggerConfig();
        assertNotNull(swaggerConfig);
    }

}
