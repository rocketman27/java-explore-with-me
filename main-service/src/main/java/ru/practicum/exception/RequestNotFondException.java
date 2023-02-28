package ru.practicum.exception;

public class RequestNotFondException extends EntityNotFoundException {

    public RequestNotFondException(String message) {
        super(message);
    }
}
