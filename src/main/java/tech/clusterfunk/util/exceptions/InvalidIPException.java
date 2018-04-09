package tech.clusterfunk.util.exceptions;

public class InvalidIPException extends Exception {

    private String message;

    public InvalidIPException(String message) {
        this.message = message;
    }

    public InvalidIPException(String message, Throwable cause) {
        super(cause);
        this.message = message;
    }

    @Override
    public String getMessage(){
        return message;
    }
}
