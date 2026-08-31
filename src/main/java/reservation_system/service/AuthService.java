package reservation_system.service;

import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import reservation_system.config.JwtService;
import reservation_system.dto.AuthResponse;
import reservation_system.dto.LoginRequest;
import reservation_system.dto.RegisterRequest;
import reservation_system.entity.RefreshToken;
import reservation_system.entity.User;
import reservation_system.entity.UserRole;
import reservation_system.repository.RefreshTokenRepository;
import reservation_system.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered.");
        }

        User user = new User(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                UserRole.STUDENT
        );

        userRepository.save(user);

        String accessToken = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());
        saveRefreshToken(user, refreshToken);

        return new AuthResponse(
                accessToken,
                refreshToken,
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials."));

        String accessToken = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        refreshTokenRepository.deleteByUserId(user.getId());
        saveRefreshToken(user, refreshToken);

        return new AuthResponse(
                accessToken,
                refreshToken,
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    public AuthResponse refresh(String refreshTokenValue) {
        if (!jwtService.isTokenValid(refreshTokenValue)) {
            throw new IllegalArgumentException("Invalid refresh token.");
        }

        String email = jwtService.extractEmail(refreshTokenValue);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        String accessToken = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        String newRefreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        refreshTokenRepository.deleteByUserId(user.getId());
        saveRefreshToken(user, newRefreshToken);

        return new AuthResponse(
                accessToken,
                newRefreshToken,
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    public void logout(String refreshTokenValue) {
        refreshTokenRepository.findByToken(refreshTokenValue)
                .ifPresent(refreshTokenRepository::delete);
    }

    private void saveRefreshToken(User user, String tokenValue) {
        RefreshToken refreshToken = new RefreshToken(
                tokenValue,
                user,
                LocalDateTime.now().plusDays(7)
        );

        refreshTokenRepository.save(refreshToken);
    }
}
