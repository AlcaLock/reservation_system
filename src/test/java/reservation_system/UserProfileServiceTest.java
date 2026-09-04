package reservation_system;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import reservation_system.dto.UpdateProfileRequest;
import reservation_system.dto.UserResponse;
import reservation_system.entity.User;
import reservation_system.entity.UserRole;
import reservation_system.repository.RefreshTokenRepository;
import reservation_system.repository.UserRepository;
import reservation_system.service.UserProfileService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private BCryptPasswordEncoder passwordEncoder;
    private UserProfileService userProfileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordEncoder = new BCryptPasswordEncoder();
        userProfileService = new UserProfileService(
                userRepository, refreshTokenRepository, passwordEncoder);
    }

    @Test
    void shouldUpdateProfileAndRevokeRefreshTokensWhenPasswordChanges() {
        User user = new User("Ana", "Lopez", "ana@example.com", "old-hash", UserRole.STUDENT);
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstName("Ana Maria");
        request.setLastName("Gomez");
        request.setPassword("new-secure-password");
        when(userRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserResponse response = userProfileService.update("ana@example.com", request);

        assertThat(response.firstName()).isEqualTo("Ana Maria");
        assertThat(response.lastName()).isEqualTo("Gomez");
        assertThat(passwordEncoder.matches("new-secure-password", user.getPassword())).isTrue();
        verify(refreshTokenRepository).deleteByUserId(user.getId());
        verify(userRepository).save(user);
    }
}
