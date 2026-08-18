package reservation_system.dto;

import reservation_system.entity.ResourceStatus;
import reservation_system.entity.ResourceType;

public class ResourceResponse {

    private Long id;
    private String name;
    private String description;
    private ResourceType type;
    private Integer capacity;
    private String location;
    private ResourceStatus status;

    public ResourceResponse(Long id, String name, String description, ResourceType type, Integer capacity, String location, ResourceStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.capacity = capacity;
        this.location = location;
        this.status = status;

    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ResourceType getType() {
        return type;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public String getLocation() {
        return location;
    }

    public ResourceStatus getStatus() {
        return status;
    }
 


    
}
