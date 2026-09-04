package reservation_system.dto;

import java.util.List;

public record AdminDashboardResponse(
        long totalUsers,
        long enabledUsers,
        long totalResources,
        long availableResources,
        long totalReservations,
        long activeReservations,
        long currentOccupancyPercentage,
        List<DailyReservationCountResponse> reservationsByDay,
        List<ResourceUsageResponse> mostUsedResources,
        List<String> recentAlerts
) {
}
