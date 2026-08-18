package reservation_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFound(
            ResourceNotFoundException exception
    ) {
        return new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage()
        );
    }

    @ExceptionHandler(InvalidResourceStatusException.class)
@ResponseStatus(HttpStatus.CONFLICT)
public ErrorResponse handleInvalidResourceStatus(
        InvalidResourceStatusException exception
) {
    return new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            exception.getMessage()
    );
}
}