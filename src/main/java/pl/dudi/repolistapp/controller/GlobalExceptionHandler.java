package pl.dudi.repolistapp.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import pl.dudi.repolistapp.dto.ErrorMessage;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WebClientResponseException.NotFound.class)
    public ResponseEntity<Object> handleWebClientNotFoundException(WebClientResponseException exception) {
        log.error("Exception: {}, HttpStatus{}",exception.getStatusText(),exception.getStatusCode());
        String message = String.format("Not found user with login: [%s]", getUserLogin(exception));
        return ResponseEntity
            .status(exception.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .body(ErrorMessage.of(exception.getStatusCode(), message));
    }

    private String getUserLogin(WebClientResponseException exception) {
        String path = Objects.requireNonNull(exception.getRequest()).getURI().getPath();
        String beginPattern = "users/";
        int beginIndex = path.indexOf(beginPattern);
        int endIndex = path.indexOf("/repos");
        return path.substring(beginIndex+beginPattern.length(), endIndex);
    }

}
