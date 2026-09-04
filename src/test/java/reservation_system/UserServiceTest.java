package reservation_system;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import reservation_system.dto.UpdateUserRoleRequest;
import reservation_system.dto.UpdateUserStatusRequest;
import reservation_system.dto.UserResponse;
import reservation_system.entity.User;
import reservation_system.entity.UserRole;
import reservation_system.repository.UserRepository;
import reservation_system.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository);
    }

    @Test
    void shouldReturnUserDataWithoutPassword() {
        User user = new User("Ana", "Lopez", "ana@example.com", "hashed-password", UserRole.STUDENT);
        when(userRepository.search(null)).thenReturn(List.of(user));

        List<UserResponse> users = userService.findAll(null);

        assertThat(users).hasSize(1);
        assertThat(users.getFirst().email()).isEqualTo("ana@example.com");
        assertThat(users.getFirst().role()).isEqualTo(UserRole.STUDENT);
        assertThat(UserResponse.class.getRecordComponents())
                .extracting(component -> component.getName())
                .doesNotContain("password");
    }

    @Test
    void shouldUpdateUserRole() {
        User user = new User("Ana", "Lopez", "ana@example.com", "hashed-password", UserRole.STUDENT);
        UpdateUserRoleRequest request = new UpdateUserRoleRequest();
        request.setRole(UserRole.ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserResponse response = userService.updateRole(1L, request);

        assertThat(response.role()).isEqualTo(UserRole.ADMIN);
        verify(userRepository).save(user);
    }

    @Test
    void shouldUpdateUserStatus() {
        User user = new User("Ana", "Lopez", "ana@example.com", "hashed-password", UserRole.STUDENT);
        UpdateUserStatusRequest request = new UpdateUserStatusRequest();
        request.setEnabled(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserResponse response = userService.updateStatus(1L, request);

        assertThat(response.enabled()).isFalse();
        verify(userRepository).save(user);
    }
}
