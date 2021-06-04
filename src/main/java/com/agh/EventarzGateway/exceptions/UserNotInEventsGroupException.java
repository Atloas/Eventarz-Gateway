package com.agh.EventarzGateway.exceptions;

public class UserNotInEventsGroupException extends RuntimeException {

    public UserNotInEventsGroupException(String message) {
        super(message);
    }
}
