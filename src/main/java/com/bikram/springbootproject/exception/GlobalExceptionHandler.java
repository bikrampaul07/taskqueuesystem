package com.bikram.springbootproject.exception;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import  java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<Map<String,Object>> handleValidation(MethodArgumentNotValidException ex){
        List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(f->f.getField() + " : " + f.getDefaultMessage()).toList();
        return build(HttpStatus.BAD_REQUEST,"Validation failed",errors);

    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        log.error("Runtime error: {}", ex.getMessage());
        HttpStatus status = ex.getMessage() != null && ex.getMessage().contains("not found")
                ? HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
        return build(status, ex.getMessage(), null);
    }

    // Bad argument errors
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArg(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    // Catch-all
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        log.error("Unexpected error", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", null);
    }

    private ResponseEntity<Map<String,Object>> build(HttpStatus status, String message, List<String> details){
       Map<String,Object> body = new LinkedHashMap<>();
       body.put("timestamp",LocalDateTime.now().toString());
       body.put("status",status.value());
       body.put("error",status.getReasonPhrase());
       body.put("message",message);
       if(details != null) body.put("details",details);
       return ResponseEntity.status(status).body(body);
   }
}
