package reservation_system.dto;

public record ResourceUsageResponse(Long resourceId, String resourceName, long reservations) {
}