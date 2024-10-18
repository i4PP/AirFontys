package me.abdul.authentication.exceptions;

public class InvalidRefreshTokenEception extends RuntimeException {
    public InvalidRefreshTokenEception(String message) {
        super(message);
    }
}
