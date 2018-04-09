package tech.clusterfunk.util.exceptions;

public class FatalException extends Exception {
    private String message = "Fatal Error: Something happened that should not happen";

    public FatalException(){}

    public FatalException(String message) {
        this.message = message;
    }

    public FatalException(String message, Throwable cause) {
        super(cause);
        this.message = message;
    }

    @Override
    public String getMessage(){
        return message;
    }
}
