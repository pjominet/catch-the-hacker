package tech.clusterfunk.util.exceptions;

public class InvalidIPException extends Exception {

    public InvalidIPException(String msg) {
        super(msg);
    }

    public InvalidIPException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
