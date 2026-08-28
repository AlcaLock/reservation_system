package reservation_system;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import reservation_system.dto.CreateReservationRequest;
import reservation_system.dto.ReservationResponse;
import reservation_system.entity.Reservation;
import reservation_system.entity.ReservationStatus;
import reservation_system.entity.Resource;
import reservation_system.entity.ResourceStatus;
import reservation_system.entity.ResourceType;
import reservation_system.entity.User;
import reservation_system.entity.UserRole;
import reservation_system.exception.ReservationStatusException;
import reservation_system.exception.UserNotFoundException;
import reservation_system.repository.ReservationRepository;
import reservation_system.repository.ResourceRepository;
import reservation_system.repository.UserRepository;
import reservation_system.service.ReservationService;

class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ResourceRepository resourceRepository;

    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reservationService = new ReservationService(
                reservationRepository,
                userRepository,
                resourceRepository
        );
    }

    @Test
    void shouldCreateActiveReservation() {
        User user = new User(
                "Ana", "Lopez", "ana@example.com", "temporary", UserRole.STUDENT
        );
        Resource resource = new Resource(
                "Sala 101", "Sala de estudio", ResourceType.ROOM, 30,
                "Edificio A", ResourceStatus.AVAILABLE
        );
        CreateReservationRequest request = requestAt(4);
        Reservation savedReservation = new Reservation(
                request.getStartTime(), request.getEndTime(), request.getPurpose(),
                ReservationStatus.ACTIVE, user, resource
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(resourceRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(resource));
        when(reservationRepository.countByUserAndStatus(user, ReservationStatus.ACTIVE))
                .thenReturn(0L);
        when(reservationRepository
                .existsByResourceAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                        resource,
                        ReservationStatus.ACTIVE,
                        request.getEndTime(),
                        request.getStartTime()
                )).thenReturn(false);
        when(reservationRepository.save(any(Reservation.class)))
                .thenReturn(savedReservation);

        ReservationResponse response = reservationService.create(request);

        assertThat(response.getStatus()).isEqualTo(ReservationStatus.ACTIVE);
        assertThat(response.getPurpose()).isEqualTo("Study session");
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void shouldRejectReservationWhenUserDoesNotExist() {
        CreateReservationRequest request = requestAt(4);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.create(request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with id 1 not found.");

        verify(resourceRepository, never()).findByIdForUpdate(2L);
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void shouldRejectReservationWhenUserAlreadyHasThreeActiveReservations() {
        User user = new User(
                "Ana", "Lopez", "ana@example.com", "temporary", UserRole.STUDENT
        );
        Resource resource = new Resource(
                "Sala 101", "Sala de estudio", ResourceType.ROOM, 30,
                "Edificio A", ResourceStatus.AVAILABLE
        );
        CreateReservationRequest request = requestAt(4);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(resourceRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(resource));
        when(reservationRepository.countByUserAndStatus(user, ReservationStatus.ACTIVE))
                .thenReturn(3L);

        assertThatThrownBy(() -> reservationService.create(request))
                .isInstanceOf(ReservationStatusException.class)
                .hasMessage("A user cannot have more than 3 active reservations.");

        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void shouldCancelActiveReservation() {
        User user = new User(
                "Ana", "Lopez", "ana@example.com", "temporary", UserRole.STUDENT
        );
        Resource resource = new Resource(
                "Sala 101", "Sala de estudio", ResourceType.ROOM, 30,
                "Edificio A", ResourceStatus.AVAILABLE
        );
        Reservation reservation = new Reservation(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2),
                "Study session", ReservationStatus.ACTIVE, user, resource
        );

        when(reservationRepository.findById(10L))
                .thenReturn(Optional.of(reservation));
        when(reservationRepository.save(reservation))
                .thenReturn(reservation);

        ReservationResponse response = reservationService.cancel(10L);

        assertThat(response.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        verify(reservationRepository).save(reservation);
    }

    @Test
    void shouldRejectCancellingNonActiveReservation() {
        User user = new User(
                "Ana", "Lopez", "ana@example.com", "temporary", UserRole.STUDENT
        );
        Resource resource = new Resource(
                "Sala 101", "Sala de estudio", ResourceType.ROOM, 30,
                "Edificio A", ResourceStatus.AVAILABLE
        );
        Reservation reservation = new Reservation(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2),
                "Study session", ReservationStatus.CANCELLED, user, resource
        );

        when(reservationRepository.findById(10L))
                .thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reservationService.cancel(10L))
                .isInstanceOf(ReservationStatusException.class)
                .hasMessage("Only active reservations can be cancelled.");

        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    

    private CreateReservationRequest requestAt(int daysFromNow) {
        CreateReservationRequest request = new CreateReservationRequest();
        request.setUserId(1L);
        request.setResourceId(2L);
        request.setStartTime(LocalDateTime.now().plusDays(daysFromNow));
        request.setEndTime(LocalDateTime.now().plusDays(daysFromNow).plusHours(2));
        request.setPurpose("Study session");
        return request;
    }
}
