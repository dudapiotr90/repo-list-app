package pl.dudi.repolistapp.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.dudi.repolistapp.dto.ErrorMessage;
import pl.dudi.repolistapp.infrastructure.exception.RequestPerHourExceededException;
import pl.dudi.repolistapp.infrastructure.exception.RuntimeExecutionException;
import pl.dudi.repolistapp.infrastructure.exception.RuntimeInterruptedException;
import pl.dudi.repolistapp.infrastructure.exception.UserNotFoundException;

import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException exception) {
        log.error("Exception: {}",exception.getMessage());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ErrorMessage(HttpStatus.NOT_FOUND.value(), exception.getMessage()));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Object> handleMissingAcceptHeader(MissingRequestHeaderException exception) {
        log.error("Exception: {}, HttpStatus{}",exception.getMessage(),exception.getStatusCode());
        String message = String.format("Header: [%s=%s] is required" , HttpHeaders.ACCEPT,MediaType.APPLICATION_JSON_VALUE);
        return ResponseEntity
            .status(exception.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ErrorMessage(exception.getStatusCode().value(), message));
    }

    @ExceptionHandler(RequestPerHourExceededException.class)
    public ResponseEntity<Object> handleRequestPerHourExceededException(RequestPerHourExceededException exception) {
        log.error("To many requests to github");
        return ResponseEntity
            .status(HttpStatus.TOO_MANY_REQUESTS)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ErrorMessage(HttpStatus.TOO_MANY_REQUESTS.value(), exception.getMessage()));
    }
    @ExceptionHandler({RuntimeInterruptedException.class, RuntimeExecutionException.class})
    public ResponseEntity<Object> handleException(Exception exception) {
        String errorId = UUID.randomUUID().toString();
        log.error("Error: {} occurred",errorId);
        String message = "Server encountered unexpected errorId: [%s]. Contact support providing errorId".formatted(errorId);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), message));
    }
}
