package reservation_system.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import reservation_system.dto.ResourceStatisticsResponse;
import reservation_system.dto.ResourceStatusCountResponse;
import reservation_system.dto.ResourceTypeCountResponse;
import reservation_system.dto.ResourceUsageResponse;
import reservation_system.entity.Reservation;
import reservation_system.entity.ReservationStatus;
import reservation_system.entity.Resource;
import reservation_system.entity.ResourceStatus;
import reservation_system.entity.ResourceType;
import reservation_system.repository.ReservationRepository;
import reservation_system.repository.ResourceRepository;

@Service
public class ResourceStatisticsService {

    private final ResourceRepository resourceRepository;
    private final ReservationRepository reservationRepository;

    public ResourceStatisticsService(
            ResourceRepository resourceRepository,
            ReservationRepository reservationRepository) {
        this.resourceRepository = resourceRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional(readOnly = true)
    public ResourceStatisticsResponse getStatistics() {
        List<Resource> resources = resourceRepository.findAll();
        List<Reservation> reservations = reservationRepository.findAllByOrderByStartTimeAsc();
        Map<Resource, Long> usage = calculateUsage(reservations);

        return new ResourceStatisticsResponse(
                resources.size(),
                resources.stream().filter(resource -> !usage.containsKey(resource)).count(),
                countByStatus(resources),
                countByType(resources),
                toUsageResponses(usage));
    }

    private Map<Resource, Long> calculateUsage(List<Reservation> reservations) {
        return reservations.stream()
                .filter(reservation -> reservation.getStatus() != ReservationStatus.CANCELLED)
                .collect(Collectors.groupingBy(Reservation::getResource, Collectors.counting()));
    }

    private List<ResourceStatusCountResponse> countByStatus(List<Resource> resources) {
        Map<ResourceStatus, Long> counts = resources.stream()
                .collect(Collectors.groupingBy(Resource::getStatus, Collectors.counting()));
        return java.util.Arrays.stream(ResourceStatus.values())
                .map(status -> new ResourceStatusCountResponse(status, counts.getOrDefault(status, 0L)))
                .toList();
    }

    private List<ResourceTypeCountResponse> countByType(List<Resource> resources) {
        Map<ResourceType, Long> counts = resources.stream()
                .collect(Collectors.groupingBy(Resource::getType, Collectors.counting()));
        return java.util.Arrays.stream(ResourceType.values())
                .map(type -> new ResourceTypeCountResponse(type, counts.getOrDefault(type, 0L)))
                .toList();
    }

    private List<ResourceUsageResponse> toUsageResponses(Map<Resource, Long> usage) {
        return usage.entrySet().stream()
                .sorted(Map.Entry.<Resource, Long>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(entry -> entry.getKey().getName()))
                .limit(5)
                .map(entry -> new ResourceUsageResponse(
                        entry.getKey().getId(), entry.getKey().getName(), entry.getValue()))
                .toList();
    }
}