package reservation_system.exception;

public class ReservationConflictException extends RuntimeException {
    
    public ReservationConflictException(){
        super("The selected time slot is already reserves.");

    
    }
}
