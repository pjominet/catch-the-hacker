package tech.clusterfunk.util.exceptions;

public class UnrecognizedCommandException extends Exception {

    private String message;

    public UnrecognizedCommandException(String message) {
        this.message = message;
    }

    public UnrecognizedCommandException(String message, Throwable cause) {
        super(cause);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
