package reservation_system.exception;

public class ResourceNotFoundException
extends RuntimeException
{

    public ResourceNotFoundException(Long Id) {
        super("Source with ID " + Id + " not found.");
    }

}
