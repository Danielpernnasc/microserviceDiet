package com.trainday.microserviceDiet.infra.Security;

import com.mongodb.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(
            BadCredentialsException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ex.getMessage());

    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<String> handleDuplicateKey(
            DuplicateKeyException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Duplicate  History alredy exists");
    }

  @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAll(
            Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getClass().getName());
    }
}
