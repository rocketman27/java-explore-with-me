package ru.practicum.exception;

public class CompilationNotFound extends EntityNotFoundException {

    public CompilationNotFound(String message) {
        super(message);
    }
}
