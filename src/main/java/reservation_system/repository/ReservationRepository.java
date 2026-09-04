package reservation_system.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import reservation_system.entity.Reservation;
import reservation_system.entity.ReservationStatus;
import reservation_system.entity.Resource;
import reservation_system.entity.User;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    boolean existsByResourceAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
            Resource resource,
            ReservationStatus status,
            LocalDateTime endTime,
            LocalDateTime startTime
    );

    List<Reservation> findAllByOrderByStartTimeAsc();

    List<Reservation> findByStartTimeLessThanAndEndTimeGreaterThanOrderByStartTimeAsc(
            LocalDateTime endTime,
            LocalDateTime startTime
    );

    List<Reservation> findByUserOrderByStartTimeDesc(
            User user
    );

    long countByUserAndStatus(
            User user,
            ReservationStatus status
    );

        long countByStatus(ReservationStatus status);

                @Query("""
                                                select count(reservation) from Reservation reservation
                                                where reservation.status = reservation_system.entity.ReservationStatus.ACTIVE
                                                        and reservation.startTime <= :currentTime
                                                        and reservation.endTime > :currentTime
                                                """)
                long countCurrentReservations(@Param("currentTime") LocalDateTime currentTime);
}
