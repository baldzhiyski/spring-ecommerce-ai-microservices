package org.baldzhiyski.order.exception;


import org.baldzhiyski.order.model.res.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handleCustomerNotFoundException(BusinessException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Customer Not Found : %s", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        HashMap<String, String> errors = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach((fieldError) -> {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(errors));
    }
}
