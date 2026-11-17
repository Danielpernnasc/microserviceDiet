package com.trainday.microserviceDiet.infra.Security;

import com.mongodb.DuplicateKeyException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void returnsOriginalStatusAndReasonForResponseStatusException() {
        ResponseStatusException exception =
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Diet not found");

        var response = handler.handleResponseStatusException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Diet not found", response.getBody());
    }

    @Test
    void returnsUnauthorizedForBadCredentials() {
        BadCredentialsException exception = new BadCredentialsException("Invalid credentials");

        var response = handler.handleBadCredentialsException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
    }

    @Test
    void returnsConflictForDuplicateKey() {
        DuplicateKeyException exception = mock(DuplicateKeyException.class);

        var response = handler.handleDuplicateKey(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Duplicate  History alredy exists", response.getBody());
    }

    @Test
    void returnsInternalServerErrorAndExceptionTypeForUnexpectedException() {
        Exception exception = new IllegalStateException("Unexpected failure");

        var response = handler.handleAll(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(IllegalStateException.class.getName(), response.getBody());
    }
}
