package reservation_system.dto;

import jakarta.validation.constraints.NotNull;
import reservation_system.entity.UserRole;

public class UpdateUserRoleRequest {

    @NotNull
    private UserRole role;

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}