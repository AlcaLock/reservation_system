package reservation_system.exception;

public class ResourceUnavailableException extends RuntimeException {
    
public ResourceUnavailableException(){
    super("Resource is not available for reservations.");

}

}
