package reservation_system.dto;

import java.time.LocalDate;

public record DailyReservationCountResponse(LocalDate date, long reservations) {
}