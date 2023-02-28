package ru.practicum.handler;

import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.ApiError;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.api.AdminController;
import ru.practicum.api.PrivateController;
import ru.practicum.api.PublicController;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.utils.DateTimeConstants;

import java.time.LocalDateTime;

import static org.openapitools.model.ApiError.StatusEnum;

@Slf4j
@RestControllerAdvice(assignableTypes = {
        AdminController.class,
        PrivateController.class,
        PublicController.class
})
public class ErrorHandler {

    @ExceptionHandler(value = {Throwable.class})
    public ResponseEntity<ApiError> handleThrowable(final Throwable e) {
        log.error("Server returned HttpCode 400: {}", e.getMessage(), e);
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setStatus(StatusEnum._400_BAD_REQUEST);
        apiError.setReason("Unexpected error.");
        apiError.setTimestamp(LocalDateTime.now().format(DateTimeConstants.DATE_TIME_FORMATTER));
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {EntityNotFoundException.class})
    public ResponseEntity<ApiError> handleNotFoundException(final EntityNotFoundException e) {
        log.error("Server returned HttpCode 404: {}", e.getMessage(), e);
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setStatus(StatusEnum._404_NOT_FOUND);
        apiError.setReason("The required object was not found.");
        apiError.setTimestamp(LocalDateTime.now().format(DateTimeConstants.DATE_TIME_FORMATTER));
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        log.error("Server returned HttpCode 409: {}", e.getMessage(), e);
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setStatus(StatusEnum._409_CONFLICT);
        apiError.setReason("Integrity constraint has been violated.");
        apiError.setTimestamp(LocalDateTime.now().format(DateTimeConstants.DATE_TIME_FORMATTER));
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.error("Server returned HttpCode 400: {}", e.getMessage(), e);
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setStatus(StatusEnum._400_BAD_REQUEST);
        apiError.setReason("Argument not valid.");
        apiError.setTimestamp(LocalDateTime.now().format(DateTimeConstants.DATE_TIME_FORMATTER));
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ConflictException.class})
    public ResponseEntity<ApiError> handleConflictException(final ConflictException e) {
        log.error("Server returned HttpCode 409: {}", e.getMessage(), e);
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setStatus(StatusEnum._409_CONFLICT);
        apiError.setReason("For the requested operation the conditions are not met.");
        apiError.setTimestamp(LocalDateTime.now().format(DateTimeConstants.DATE_TIME_FORMATTER));
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {MissingServletRequestParameterException.class})
    public ResponseEntity<ApiError> handleMissingServletRequestParameterException(final MissingServletRequestParameterException e) {
        log.error("Server returned HttpCode 400: {}", e.getMessage(), e);
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setStatus(StatusEnum._400_BAD_REQUEST);
        apiError.setReason("Required parameters missed.");
        apiError.setTimestamp(LocalDateTime.now().format(DateTimeConstants.DATE_TIME_FORMATTER));
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
}
