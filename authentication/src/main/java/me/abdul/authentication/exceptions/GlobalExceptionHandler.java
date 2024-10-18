package me.abdul.authentication.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {


    // Handle custom UserAlreadyExistException
    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<Object> handleUserAlreadyExistException(UserAlreadyExistException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "Conflict");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }
    // Handle custom InvalidCredentialsException
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Object> handleInvalidCredentialsException(InvalidCredentialsException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", "Unauthorized");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    // Handle custom InvalidRefreshTokenEception
    @ExceptionHandler(InvalidRefreshTokenEception.class)
    public ResponseEntity<Object> handleInvalidRefreshTokenEception(InvalidRefreshTokenEception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", "Unauthorized");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    // validation errors
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(org.springframework.web.bind.MethodArgumentNotValidException ex, WebRequest request){
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation error");
        body.put("message", ex.getBindingResult().getFieldError().getDefaultMessage());
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }



    // Handle any other exceptions (fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Error");
        body.put("message", "An unexpected error occurred.");
        body.put("details", ex.getMessage());
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}