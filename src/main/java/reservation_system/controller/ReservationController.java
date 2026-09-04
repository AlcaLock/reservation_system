package reservation_system.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import reservation_system.dto.CreateReservationRequest;
import reservation_system.dto.ReservationResponse;
import reservation_system.repository.UserRepository;
import reservation_system.service.ReservationService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final UserRepository userRepository;

    public ReservationController(
            ReservationService reservationService,
            UserRepository userRepository) {
        this.reservationService = reservationService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> create(
            @Valid @RequestBody CreateReservationRequest request,
            Authentication authentication
    ) {
        Long authenticatedUserId = getAuthenticatedUserId(authentication);
        if (authenticatedUserId != null) {
            request.setUserId(authenticatedUserId);
        }

        ReservationResponse response =
                reservationService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public java.util.List<ReservationResponse> findAll(
            @RequestParam(required = false) LocalDateTime start,
            @RequestParam(required = false) LocalDateTime end) {
        if (start != null || end != null) {
            if (start == null || end == null) {
                throw new IllegalArgumentException("Both start and end parameters are required.");
            }
            return reservationService.findByTimeRange(start, end);
        }
        return reservationService.findAll();
    }

    @GetMapping("/{id}")
    public ReservationResponse findById(@PathVariable Long id) {
        return reservationService.findById(id);
    }

    @GetMapping("/my")
    public java.util.List<ReservationResponse> findByUser(
            @RequestParam(required = false) Long userId,
            Authentication authentication
    ) {
        Long authenticatedUserId = getAuthenticatedUserId(authentication);
        if (authenticatedUserId != null) {
            return reservationService.findByUserId(authenticatedUserId);
        }

        if (userId == null) {
            throw new IllegalArgumentException("userId is required when security is disabled.");
        }

        return reservationService.findByUserId(userId);
    }

    @PatchMapping("/{id}/cancel")
    public ReservationResponse cancel(@PathVariable Long id, Authentication authentication) {
        Long authenticatedUserId = getAuthenticatedUserId(authentication);
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        return reservationService.cancel(id, authenticatedUserId, isAdmin);
    }

    private Long getAuthenticatedUserId(Authentication authentication) {
        if (authentication == null
            || !authentication.isAuthenticated()
            || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found."))
                .getId();
    }
}