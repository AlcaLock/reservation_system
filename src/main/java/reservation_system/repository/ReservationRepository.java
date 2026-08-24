package reservation_system.repository;

import java.time.LocalDateTime;


import org.springframework.data.jpa.repository.JpaRepository;

import reservation_system.entity.Reservation;
import reservation_system.entity.ReservationStatus;
import reservation_system.entity.Resource;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    boolean existsByResourceAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
            Resource resource,
            ReservationStatus status,
            LocalDateTime endTime,
            LocalDateTime startTime
    );
}
