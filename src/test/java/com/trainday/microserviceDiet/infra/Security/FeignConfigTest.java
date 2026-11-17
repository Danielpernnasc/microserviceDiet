package com.trainday.microserviceDiet.infra.Security;

import com.trainday.microserviceDiet.infra.Config.FeignConfig;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class FeignConfigTest {

    private final RequestInterceptor interceptor = new FeignConfig().requestInterceptor();

    @BeforeEach
    void clearRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

    @AfterEach
    void resetRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void forwardsAuthorizationHeaderFromCurrentRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer test-token");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        RequestTemplate template = new RequestTemplate();

        interceptor.apply(template);

        assertEquals(List.of("Bearer test-token"), List.copyOf(template.headers().get("Authorization")));
    }

    @Test
    void doesNotAddAuthorizationHeaderWhenRequestHasNone() {
        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(new MockHttpServletRequest()));
        RequestTemplate template = new RequestTemplate();

        interceptor.apply(template);

        assertFalse(template.headers().containsKey("Authorization"));
    }

    @Test
    void doesNotAddAuthorizationHeaderWhenThereIsNoRequestContext() {
        RequestTemplate template = new RequestTemplate();

        interceptor.apply(template);

        assertFalse(template.headers().containsKey("Authorization"));
    }
}
