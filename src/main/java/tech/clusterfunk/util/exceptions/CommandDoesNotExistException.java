package tech.clusterfunk.util.exceptions;

public class CommandDoesNotExistException extends Exception {

    public CommandDoesNotExistException(String msg) {
        super(msg);
    }

    public CommandDoesNotExistException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
