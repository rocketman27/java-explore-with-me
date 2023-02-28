package ru.practicum.exception;

public class ExceptionUtils {

    public static UserNotFoundException getUserNotFoundException(long userId) {
        return new UserNotFoundException(String.format("User with id=%s is not found, cannot create event.", userId));
    }

    public static CategoryNotFoundException getCategoryNotFoundException(long catId) {
        return new CategoryNotFoundException(String.format("Category with id=%s is not found, cannot create or update even.", catId));
    }

    public static EventNotFoundException getEventNotFoundException(long eventId) {
        return new EventNotFoundException(String.format("Event with id=%s is not found.", eventId));
    }

    public static RequestNotFondException getRequestNotFoundException(long requestId) {
        return new RequestNotFondException(String.format("Request with id=%s is not found.", requestId));
    }

    public static CompilationNotFound getCompilationNotFound(long compId) {
        return new CompilationNotFound(String.format("Compilation with id=%s is not found.", compId));
    }
}
