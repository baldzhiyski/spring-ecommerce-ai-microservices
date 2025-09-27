package org.baldzhiyski.order.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.baldzhiyski.order.model.res.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handleBusinessException(BusinessException e) {
        // If BusinessException always means 404 for you, keep it like this.
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Customer Not Found : " + e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        HashMap<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errors));
    }

    // NEW: catch Feign exceptions and pass-through downstream status + message
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, Object>> handleFeign(FeignException ex) {
        int status = ex.status(); // 404 in your case
        String raw = ex.contentUTF8(); // OpenFeign 12+: convenient accessor

        // Fallback if not available:
        if (raw == null || raw.isBlank()) {
            raw = ex.responseBody()
                    .map(buf -> StandardCharsets.UTF_8.decode(buf).toString())
                    .orElse("");
        }

        // Try to mirror Customer service response if JSON
        Map<String, Object> body = new HashMap<>();
        if (!raw.isBlank()) {
            try {
                JsonNode node = objectMapper.readTree(raw);
                // copy known fields if present
                if (node.has("message")) body.put("message", node.get("message").asText());
                if (node.has("status"))  body.put("status",  node.get("status").asText());
            } catch (Exception ignored) {
                // not JSON → use raw string
            }
        }
        if (body.isEmpty()) {
            body.put("message", ex.getMessage());
        }

        return ResponseEntity.status(status).body(body);
    }
}
