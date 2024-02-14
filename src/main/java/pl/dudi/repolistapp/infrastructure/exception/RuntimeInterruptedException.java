package pl.dudi.repolistapp.infrastructure.exception;

public class RuntimeInterruptedException extends RuntimeException{
    public RuntimeInterruptedException(String message) {
        super(message);
    }
}
