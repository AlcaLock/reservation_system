package reservation_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(IllegalArgumentException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ErrorResponse handleIllegalArgument(IllegalArgumentException exception) {
                return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public ErrorResponse handleResourceNotFound(
                        ResourceNotFoundException exception) {
                return new ErrorResponse(
                                HttpStatus.NOT_FOUND.value(),
                                exception.getMessage());
        }

        @ExceptionHandler(InvalidResourceStatusException.class)
        @ResponseStatus(HttpStatus.CONFLICT)
        public ErrorResponse handleInvalidResourceStatus(
                        InvalidResourceStatusException exception) {
                return new ErrorResponse(
                                HttpStatus.CONFLICT.value(),
                                exception.getMessage());
        }

        @ExceptionHandler(ResourceUnavailableException.class)
        @ResponseStatus(HttpStatus.CONFLICT)
        public ErrorResponse handleResourceUnavailable(
                        ResourceUnavailableException ex) {
                return new ErrorResponse(
                                HttpStatus.CONFLICT.value(),
                                ex.getMessage());
        }

        @ExceptionHandler(ReservationConflictException.class)
        @ResponseStatus(HttpStatus.CONFLICT)
        public ErrorResponse handleReservationConflict(
                        ReservationConflictException ex) {
                return new ErrorResponse(
                                HttpStatus.CONFLICT.value(),
                                ex.getMessage());
        }

        @ExceptionHandler(InvalidReservationTimeException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ErrorResponse handleInvalidTime(
                        InvalidReservationTimeException ex) {
                return new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                ex.getMessage());
        }

                @ExceptionHandler(UserNotFoundException.class)
                @ResponseStatus(HttpStatus.NOT_FOUND)
                public ErrorResponse handleUserNotFound(UserNotFoundException ex) {
                        return new ErrorResponse(
                                        HttpStatus.NOT_FOUND.value(), ex.getMessage());
                }

                @ExceptionHandler(ReservationNotFoundException.class)
                @ResponseStatus(HttpStatus.NOT_FOUND)
                public ErrorResponse handleReservationNotFound(
                                ReservationNotFoundException ex) {
                        return new ErrorResponse(
                                        HttpStatus.NOT_FOUND.value(), ex.getMessage());
                }

                @ExceptionHandler(ReservationStatusException.class)
                @ResponseStatus(HttpStatus.CONFLICT)
                public ErrorResponse handleReservationStatus(
                                ReservationStatusException ex) {
                        return new ErrorResponse(
                                        HttpStatus.CONFLICT.value(), ex.getMessage());
                }
}