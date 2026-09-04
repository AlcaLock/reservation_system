package reservation_system.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import reservation_system.dto.AdminDashboardResponse;
import reservation_system.dto.DailyReservationCountResponse;
import reservation_system.dto.ResourceUsageResponse;
import reservation_system.entity.Reservation;
import reservation_system.entity.ReservationStatus;
import reservation_system.entity.Resource;
import reservation_system.entity.ResourceStatus;
import reservation_system.repository.ReservationRepository;
import reservation_system.repository.ResourceRepository;
import reservation_system.repository.UserRepository;

@Service
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final ReservationRepository reservationRepository;

    public AdminDashboardService(
            UserRepository userRepository,
            ResourceRepository resourceRepository,
            ReservationRepository reservationRepository) {
        this.userRepository = userRepository;
        this.resourceRepository = resourceRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboard() {
        LocalDate today = LocalDate.now();
        List<Reservation> reservations = reservationRepository.findAllByOrderByStartTimeAsc();
        long totalResources = resourceRepository.count();
        long availableResources = resourceRepository.countByStatus(ResourceStatus.AVAILABLE);
        long currentReservations = reservationRepository.countCurrentReservations(LocalDateTime.now());

        return new AdminDashboardResponse(
                userRepository.count(),
                userRepository.countByEnabledTrue(),
                totalResources,
                availableResources,
                reservationRepository.count(),
                reservationRepository.countByStatus(ReservationStatus.ACTIVE),
                calculateOccupancy(currentReservations, totalResources),
                reservationsByDay(reservations, today),
                mostUsedResources(reservations),
                resourceAlerts());
    }

    private long calculateOccupancy(long currentReservations, long totalResources) {
        if (totalResources == 0) {
            return 0;
        }
        return Math.round((currentReservations * 100.0) / totalResources);
    }

    private List<DailyReservationCountResponse> reservationsByDay(
            List<Reservation> reservations,
            LocalDate today) {
        LocalDate firstDay = today.minusDays(6);
        Map<LocalDate, Long> counts = reservations.stream()
                .filter(reservation -> reservation.getStatus() != ReservationStatus.CANCELLED)
                .map(Reservation::getStartTime)
                .map(LocalDateTime::toLocalDate)
                .filter(date -> !date.isBefore(firstDay) && !date.isAfter(today))
                .collect(Collectors.groupingBy(date -> date, Collectors.counting()));

        return IntStream.rangeClosed(0, 6)
                .mapToObj(firstDay::plusDays)
                .map(date -> new DailyReservationCountResponse(date, counts.getOrDefault(date, 0L)))
                .toList();
    }

    private List<ResourceUsageResponse> mostUsedResources(List<Reservation> reservations) {
        return reservations.stream()
                .filter(reservation -> reservation.getStatus() != ReservationStatus.CANCELLED)
                .collect(Collectors.groupingBy(Reservation::getResource, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Resource, Long>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(entry -> entry.getKey().getName()))
                .limit(5)
                .map(entry -> new ResourceUsageResponse(
                        entry.getKey().getId(), entry.getKey().getName(), entry.getValue()))
                .toList();
    }

    private List<String> resourceAlerts() {
        return resourceRepository.findAll().stream()
                .filter(resource -> resource.getStatus() != ResourceStatus.AVAILABLE)
                .sorted(Comparator.comparing(Resource::getName))
                .limit(5)
                .map(resource -> resource.getName() + " is " + resource.getStatus())
                .toList();
    }
}