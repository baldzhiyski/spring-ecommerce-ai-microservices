package org.baldzhiyski.product.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.baldzhiyski.product.model.res.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /* ---------- Common error envelope ---------- */
    @Value
    @Builder
    static class ApiError {
        String timestamp;       // ISO-8601
        int status;             // 404
        String error;           // "Not Found"
        String message;         // "Product 90 not found"
        String path;            // "/api/v1/products/inventory/reserve"
        Map<String, Object> details; // optional (validation field errors, etc.)
    }

    private static ResponseEntity<ApiError> build(HttpServletRequest req, HttpStatus status, String message, Map<String, Object> details) {
        return ResponseEntity.status(status).body(
                ApiError.builder()
                        .timestamp(OffsetDateTime.now().toString())
                        .status(status.value())
                        .error(status.getReasonPhrase())
                        .message(message)
                        .path(req != null ? req.getRequestURI() : null)
                        .details(details == null || details.isEmpty() ? null : details)
                        .build()
        );
    }

    /* ---------- Specific handlers ---------- */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException e, HttpServletRequest req) {
        Map<String, Object> details = new LinkedHashMap<>();
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fe : e.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }
        details.put("fieldErrors", fieldErrors);
        return build(req, HttpStatus.BAD_REQUEST, "Validation failed", details);
    }

    @ExceptionHandler(ProductPurchaseException.class)
    public ResponseEntity<ApiError> handleProductPurchase(ProductPurchaseException e, HttpServletRequest req) {
        return build(req, HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException e, HttpServletRequest req) {
        return build(req, HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFound(EntityNotFoundException e, HttpServletRequest req) {
        return build(req, HttpStatus.NOT_FOUND, e.getMessage(), null);
    }

    /* ---------- Fallback (unexpected) ---------- */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception e, HttpServletRequest req) {
        log.error("Unhandled error at {}: {}", req.getRequestURI(), e.getMessage(), e);
        return build(req, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", null);
    }
}
