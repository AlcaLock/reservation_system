package reservation_system.dto;

import java.util.List;

public record ResourceStatisticsResponse(
        long totalResources,
        long resourcesWithoutReservations,
        List<ResourceStatusCountResponse> resourcesByStatus,
        List<ResourceTypeCountResponse> resourcesByType,
        List<ResourceUsageResponse> mostUsedResources
) {
}