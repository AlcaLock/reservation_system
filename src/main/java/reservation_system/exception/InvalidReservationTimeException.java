package reservation_system.exception;

public class InvalidReservationTimeException extends RuntimeException{
    
public InvalidReservationTimeException(){
        super("End time must be after start time.");
    }

    public InvalidReservationTimeException(String message) {
        super(message);
    }

}
