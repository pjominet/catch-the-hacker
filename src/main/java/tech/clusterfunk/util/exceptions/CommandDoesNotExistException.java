package tech.clusterfunk.util.exceptions;

public class CommandDoesNotExistException extends Exception {

    private String message;

    public CommandDoesNotExistException(String message) {
        this.message = message;
    }

    public CommandDoesNotExistException(String message, Throwable cause) {
        super(cause);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
