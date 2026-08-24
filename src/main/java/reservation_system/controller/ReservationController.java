package reservation_system.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import reservation_system.dto.CreateReservationRequest;
import reservation_system.dto.ReservationResponse;
import reservation_system.service.ReservationService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> create(
            @Valid @RequestBody CreateReservationRequest request
    ) {

        ReservationResponse response =
                reservationService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}