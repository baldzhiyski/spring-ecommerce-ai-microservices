package org.baldzhiyski.payment.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.*;

@AllArgsConstructor
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;
    private static final int RAW_BODY_LIMIT = 4096;

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleBusinessException(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Payment Not Found : " + e.getMessage());
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, Object>> handleFeign(FeignException ex) {
        // Upstream status -> try to map to Spring HttpStatus, else use 502
        int upstreamStatus = ex.status();
        HttpStatus status = Optional.ofNullable(HttpStatus.resolve(upstreamStatus))
                .orElse(HttpStatus.BAD_GATEWAY);

        // Method + URL context (if available)
        String method = ex.request() != null ? ex.request().httpMethod().name() : "UNKNOWN";
        String url = ex.request() != null ? ex.request().url() : "UNKNOWN";

        // Extract charset from headers; default to UTF-8
        Charset charset = extractCharset(ex.responseHeaders())
                .orElse(StandardCharsets.UTF_8);

        // Read body bytes safely
        byte[] bytes = ex.responseBody()
                .map(ByteBuffer::array)
                .orElseGet(() -> {
                    try { return Optional.ofNullable(ex.content()).orElse(new byte[0]); }
                    catch (Throwable ignore) { return new byte[0]; }
                });

        String raw = safeDecode(bytes, charset);
        if (raw.length() > RAW_BODY_LIMIT) {
            raw = raw.substring(0, RAW_BODY_LIMIT) + "… [truncated]";
        }

        // Try to extract a human message
        String message = extractMessageFromRaw(raw);
        if (isBlank(message)) {
            message = ex.getMessage() != null ? ex.getMessage() : status.getReasonPhrase();
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("upstreamStatus", upstreamStatus);
        body.put("method", method);
        body.put("url", url);
        if (!isBlank(raw)) {
            // include short upstream body to help clients/debug (trimmed above)
            body.put("upstreamBody", raw);
        }

        log.warn("Feign upstream error: {} {} -> {} (mapped {})", method, url, upstreamStatus, status);
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    /* ===================== helpers ===================== */

    private String extractMessageFromRaw(String raw) {
        if (isBlank(raw)) return "";

        // Try JSON bodies first
        try {
            JsonNode node = objectMapper.readTree(raw);

            // RFC 7807 Problem Details
            // { "type": "...", "title": "...", "status": 400, "detail": "..." }
            String title = text(node, "title");
            String detail = text(node, "detail");
            if (!isBlank(title) || !isBlank(detail)) {
                return joinNonBlank(" - ", title, detail);
            }

            // Spring default error body
            // { "timestamp": "...", "status": 400, "error": "Bad Request", "message": "X", "path": "/..." }
            String springMessage = text(node, "message");
            if (!isBlank(springMessage)) return springMessage;

            // OAuth-ish
            // { "error": "invalid_request", "error_description": "..." }
            String errDesc = text(node, "error_description");
            String err = text(node, "error");
            if (!isBlank(errDesc) || !isBlank(err)) {
                return joinNonBlank(": ", err, errDesc);
            }

            // Validation arrays
            // { "errors": [ {"defaultMessage":"..."}, ... ] }
            if (node.hasNonNull("errors") && node.get("errors").isArray() && !node.get("errors").isEmpty()) {
                JsonNode first = node.get("errors").get(0);
                String dm = text(first, "defaultMessage");
                if (!isBlank(dm)) return dm;
                String m = text(first, "message");
                if (!isBlank(m)) return m;
            }

            // Problem+JSON violations
            // { "violations": [ {"message": "..."} ] }
            if (node.hasNonNull("violations") && node.get("violations").isArray() && !node.get("violations").isEmpty()) {
                String vmsg = text(node.get("violations").get(0), "message");
                if (!isBlank(vmsg)) return vmsg;
            }

            // Common custom fields
            for (String k : List.of("msg", "reason", "description", "detailMessage", "cause")) {
                String v = text(node, k);
                if (!isBlank(v)) return v;
            }

            // Unknown JSON shape → return raw JSON
            return raw;

        } catch (Exception ignore) {
            // Not JSON → return plain text
            return raw;
        }
    }

    private static Optional<Charset> extractCharset(Map<String, Collection<String>> headers) {
        try {
            Collection<String> cts = headers.getOrDefault(HttpHeaders.CONTENT_TYPE, List.of());
            for (String ct : cts) {
                if (ct == null) continue;
                // e.g., "application/json; charset=UTF-8"
                for (String part : ct.split(";")) {
                    String p = part.trim();
                    int eq = p.indexOf('=');
                    if (eq > 0) {
                        String key = p.substring(0, eq).trim().toLowerCase(Locale.ROOT);
                        String val = p.substring(eq + 1).trim().replace("\"", "");
                        if ("charset".equals(key)) {
                            return Optional.of(Charset.forName(val));
                        }
                    }
                }
            }
        } catch (Throwable ignore) {}
        return Optional.empty();
    }

    private static String safeDecode(byte[] bytes, Charset cs) {
        if (bytes == null || bytes.length == 0) return "";
        try { return new String(bytes, cs); } catch (Throwable e) { return ""; }
    }

    private static String text(JsonNode node, String field) {
        if (node == null) return null;
        JsonNode v = node.get(field);
        return (v == null || v.isNull()) ? null : v.asText();
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private static String joinNonBlank(String sep, String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (isBlank(p)) continue;
            if (sb.length() > 0) sb.append(sep);
            sb.append(p);
        }
        return sb.toString();
    }

}
