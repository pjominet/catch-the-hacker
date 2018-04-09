package tech.clusterfunk.util.exceptions;

public class UnknownIPException extends Exception {

    public UnknownIPException(String msg) {
        super(msg);
    }

    public UnknownIPException(String msg, Throwable cause) {
        super(msg, cause);
    }


}
