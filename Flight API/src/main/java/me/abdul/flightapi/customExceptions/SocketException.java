package me.abdul.flightapi.customExceptions;

public class SocketException extends RuntimeException {
    public SocketException(String message) {
        super(message);
    }

    public SocketException(String message, Throwable cause) {
        super(message, cause);
    }
}
