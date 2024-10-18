package me.abdul.flightapi.customExceptions;

public class FlightServiceException extends RuntimeException {
    public FlightServiceException(String message) {
        super(message);
    }
}