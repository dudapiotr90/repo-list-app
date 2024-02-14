package pl.dudi.repolistapp.infrastructure.exception;

public class RequestPerHourExceededException extends RuntimeException{
    public RequestPerHourExceededException(String message) {
        super(message);
    }
}
