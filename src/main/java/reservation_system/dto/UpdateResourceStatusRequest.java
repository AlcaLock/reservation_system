package reservation_system.dto;

import jakarta.validation.constraints.NotNull;
import reservation_system.entity.ResourceStatus;

public class UpdateResourceStatusRequest {

    @NotNull
    private ResourceStatus status;

    public UpdateResourceStatusRequest() {
    }

    public ResourceStatus getStatus() {
        return status;
    }

    public void setStatus(ResourceStatus status) {
        this.status = status;
    } 
}
