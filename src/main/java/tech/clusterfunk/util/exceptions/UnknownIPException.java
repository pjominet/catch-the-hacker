package tech.clusterfunk.util.exceptions;

public class UnknownIPException extends Exception {

    private String message;

    public UnknownIPException(String message) {
        this.message = message;
    }

    public UnknownIPException(String message, Throwable cause) {
        super(cause);
        this.message = message;
    }

    @Override
    public String getMessage(){
        return message;
    }
}
