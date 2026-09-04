package reservation_system;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import reservation_system.dto.ResourceStatisticsResponse;
import reservation_system.entity.Resource;
import reservation_system.entity.ResourceStatus;
import reservation_system.entity.ResourceType;
import reservation_system.repository.ReservationRepository;
import reservation_system.repository.ResourceRepository;
import reservation_system.service.ResourceStatisticsService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class ResourceStatisticsServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private ReservationRepository reservationRepository;

    private ResourceStatisticsService resourceStatisticsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resourceStatisticsService = new ResourceStatisticsService(
                resourceRepository, reservationRepository);
    }

    @Test
    void shouldReturnAllResourceTypeAndStatusCounts() {
        Resource room = new Resource(
                "Room A", "Meeting room", ResourceType.ROOM, 10, "Building A", ResourceStatus.AVAILABLE);
        Resource equipment = new Resource(
                "Projector", "4K projector", ResourceType.EQUIPMENT, 1, "Building B", ResourceStatus.MAINTENANCE);
        when(resourceRepository.findAll()).thenReturn(List.of(room, equipment));
        when(reservationRepository.findAllByOrderByStartTimeAsc()).thenReturn(List.of());

        ResourceStatisticsResponse statistics = resourceStatisticsService.getStatistics();

        assertThat(statistics.totalResources()).isEqualTo(2);
        assertThat(statistics.resourcesWithoutReservations()).isEqualTo(2);
        assertThat(statistics.resourcesByStatus())
                .filteredOn(count -> count.status() == ResourceStatus.AVAILABLE)
                .extracting(count -> count.resources())
                .containsExactly(1L);
        assertThat(statistics.resourcesByStatus())
                .filteredOn(count -> count.status() == ResourceStatus.MAINTENANCE)
                .extracting(count -> count.resources())
                .containsExactly(1L);
        assertThat(statistics.resourcesByType()).hasSize(ResourceType.values().length);
        assertThat(statistics.mostUsedResources()).isEmpty();
    }
}
