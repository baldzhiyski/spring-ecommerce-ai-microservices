package org.baldzhiyski.product.exception;

import jakarta.persistence.EntityNotFoundException;
import org.baldzhiyski.product.model.JSONResponse;
import org.baldzhiyski.product.model.res.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;

@ControllerAdvice
public class GlobalExceptionHandler {

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

    @ExceptionHandler(ProductPurchaseException.class)
    public JSONResponse handleProductPurchaseException(ProductPurchaseException ex) {
        return  JSONResponse
                .builder()
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.toString())
                .build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public JSONResponse handleEntityNotFoundException(EntityNotFoundException ex) {
        return  JSONResponse
                .builder()
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.toString())
                .build();
    }
}
