package reservation_system.dto;

import java.time.LocalDateTime;

import reservation_system.entity.UserRole;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        UserRole role,
        Boolean enabled,
        LocalDateTime createdAt
) {
}