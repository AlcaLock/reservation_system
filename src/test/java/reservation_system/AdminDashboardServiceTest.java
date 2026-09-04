package reservation_system;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import reservation_system.dto.AdminDashboardResponse;
import reservation_system.entity.ReservationStatus;
import reservation_system.entity.ResourceStatus;
import reservation_system.repository.ReservationRepository;
import reservation_system.repository.ResourceRepository;
import reservation_system.repository.UserRepository;
import reservation_system.service.AdminDashboardService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class AdminDashboardServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private ReservationRepository reservationRepository;

    private AdminDashboardService adminDashboardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminDashboardService = new AdminDashboardService(
                userRepository, resourceRepository, reservationRepository);
    }

    @Test
    void shouldReturnDashboardMetricsAndSevenDaySeries() {
        when(userRepository.count()).thenReturn(12L);
        when(userRepository.countByEnabledTrue()).thenReturn(10L);
        when(resourceRepository.count()).thenReturn(8L);
        when(resourceRepository.countByStatus(ResourceStatus.AVAILABLE)).thenReturn(6L);
        when(reservationRepository.count()).thenReturn(25L);
        when(reservationRepository.countByStatus(ReservationStatus.ACTIVE)).thenReturn(4L);
        when(reservationRepository.countCurrentReservations(org.mockito.ArgumentMatchers.any()))
                .thenReturn(2L);
        when(reservationRepository.findAllByOrderByStartTimeAsc()).thenReturn(List.of());
        when(resourceRepository.findAll()).thenReturn(List.of());

        AdminDashboardResponse dashboard = adminDashboardService.getDashboard();

        assertThat(dashboard.totalUsers()).isEqualTo(12);
        assertThat(dashboard.enabledUsers()).isEqualTo(10);
        assertThat(dashboard.totalResources()).isEqualTo(8);
        assertThat(dashboard.availableResources()).isEqualTo(6);
        assertThat(dashboard.totalReservations()).isEqualTo(25);
        assertThat(dashboard.activeReservations()).isEqualTo(4);
        assertThat(dashboard.currentOccupancyPercentage()).isEqualTo(25);
        assertThat(dashboard.reservationsByDay()).hasSize(7);
        assertThat(dashboard.reservationsByDay()).allSatisfy(day ->
                assertThat(day.reservations()).isZero());
    }
}
