package org.baldzhiyski.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleBusinessException(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Payment Not Found : " + e.getMessage());
    }

}
