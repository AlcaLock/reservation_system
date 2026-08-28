package reservation_system.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import reservation_system.dto.CreateReservationRequest;
import reservation_system.dto.ReservationResponse;
import reservation_system.entity.Reservation;
import reservation_system.entity.ReservationStatus;
import reservation_system.entity.Resource;
import reservation_system.entity.ResourceStatus;
import reservation_system.entity.User;
import reservation_system.exception.InvalidReservationTimeException;
import reservation_system.exception.ReservationConflictException;
import reservation_system.exception.ResourceNotFoundException;
import reservation_system.exception.ResourceUnavailableException;
import reservation_system.exception.ReservationNotFoundException;
import reservation_system.exception.ReservationStatusException;
import reservation_system.exception.UserNotFoundException;
import reservation_system.repository.ReservationRepository;
import reservation_system.repository.ResourceRepository;
import reservation_system.repository.UserRepository;


@Service
public class ReservationService {
    
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
  
    public ReservationService(
            ReservationRepository reservationRepository,
            UserRepository userRepository,
            ResourceRepository resourceRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.resourceRepository = resourceRepository;
    }


@Transactional
public ReservationResponse create(CreateReservationRequest request) {
    User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new UserNotFoundException(request.getUserId()));

    Resource resource = resourceRepository.findByIdForUpdate(request.getResourceId())
            .orElseThrow(() -> new ResourceNotFoundException(request.getResourceId()));

    if (resource.getStatus() != ResourceStatus.AVAILABLE) {
        throw new ResourceUnavailableException();
    }

    if (!request.getEndTime().isAfter(request.getStartTime())) {
        throw new InvalidReservationTimeException();
    }

    if (request.getStartTime().isBefore(LocalDateTime.now())) {
        throw new InvalidReservationTimeException("Start time cannot be in the past.");
    }

    long activeReservations = reservationRepository.countByUserAndStatus(
            user,
            ReservationStatus.ACTIVE
    );

    if (activeReservations >= 3) {
        throw new ReservationStatusException(
                "A user cannot have more than 3 active reservations."
        );
    }

    boolean overlap = reservationRepository
            .existsByResourceAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                    resource,
                    ReservationStatus.ACTIVE,
                    request.getEndTime(),
                    request.getStartTime()
            );

    if (overlap) {
        throw new ReservationConflictException();
    }

    Reservation reservation = new Reservation(
            request.getStartTime(),
            request.getEndTime(),
            request.getPurpose(),
            ReservationStatus.ACTIVE,
            user,
            resource
    );

    Reservation saved = reservationRepository.save(reservation);

    return toResponse(saved);
}

@Transactional(readOnly = true)
public List<ReservationResponse> findAll() {
    return reservationRepository.findAllByOrderByStartTimeAsc()
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
}

@Transactional(readOnly = true)
public ReservationResponse findById(Long id) {
    return toResponse(reservationRepository.findById(id)
            .orElseThrow(() -> new ReservationNotFoundException(id)));
}

@Transactional(readOnly = true)
public List<ReservationResponse> findByUserId(Long userId) {
    User user = findUser(userId);

    return reservationRepository.findByUserOrderByStartTimeDesc(user)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
}

@Transactional
public ReservationResponse cancel(Long id) {
    Reservation reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new ReservationNotFoundException(id));

    if (reservation.getStatus() != ReservationStatus.ACTIVE) {
        throw new ReservationStatusException(
                "Only active reservations can be cancelled."
        );
    }

    reservation.setStatus(ReservationStatus.CANCELLED);
    return toResponse(reservationRepository.save(reservation));
}

private User findUser(Long userId) {
    return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
}

    private ReservationResponse toResponse(Reservation reservation) {

        return new ReservationResponse(
                reservation.getId(),
                reservation.getUser().getId(),
                reservation.getResource().getId(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getPurpose(),
                reservation.getStatus());
    }

}
