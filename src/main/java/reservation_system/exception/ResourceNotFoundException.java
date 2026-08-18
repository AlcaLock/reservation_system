package reservation_system.exception;

public class ResourceNotFoundException
extends RuntimeException
{

    public ResourceNotFoundException(Long Id) {
        super("Resource with id " + Id + " not found.");
    }

}
