package reservation_system.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            .orElseThrow(() ->
                    new ResourceNotFoundException(request.getUserId()));

    Resource resource = resourceRepository.findById(request.getResourceId())
            .orElseThrow(() ->
                    new ResourceNotFoundException(request.getResourceId()));

    if (resource.getStatus() != ResourceStatus.AVAILABLE) {
        throw new ResourceUnavailableException();
    }

    if (!request.getEndTime().isAfter(request.getStartTime())) {
        throw new InvalidReservationTimeException();
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
