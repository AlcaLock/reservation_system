package reservation_system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import reservation_system.entity.ResourceType;

public class UpdateResourceRequest {
    
 @NotBlank
   private String name;
   
   private String description;

   @NotNull
   private ResourceType type;

   @NotNull
   @Min(1)
   private Integer capacity;

   @NotBlank
   String location;

    public UpdateResourceRequest() {
    }

public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

