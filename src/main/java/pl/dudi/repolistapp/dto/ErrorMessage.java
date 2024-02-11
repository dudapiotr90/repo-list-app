package pl.dudi.repolistapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Value
@Builder
@AllArgsConstructor(staticName = "of")
public class ErrorMessage {

    HttpStatusCode status;
    String message;
}
