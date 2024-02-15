package pl.dudi.repolistapp.infrastructure.exception;

public class RuntimeExecutionException extends RuntimeException{
    public RuntimeExecutionException(String message) {
        super(message);
    }

    public RuntimeExecutionException(Throwable cause) {
        super(cause);
    }
}
