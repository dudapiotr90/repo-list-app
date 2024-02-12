package pl.dudi.repolistapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class ErrorMessage {

    int status;
    String message;
}
