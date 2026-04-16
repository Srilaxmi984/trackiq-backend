
package com.trackiq.backend.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex) {

        return ResponseEntity
                .badRequest() // ✅ 400 instead of 403
                .body(Map.of(
                        "message", ex.getMessage()
                ));
    }
}