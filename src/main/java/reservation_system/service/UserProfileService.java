package reservation_system.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import reservation_system.dto.UpdateProfileRequest;
import reservation_system.dto.UserResponse;
import reservation_system.entity.User;
import reservation_system.exception.UserNotFoundException;
import reservation_system.repository.RefreshTokenRepository;
import reservation_system.repository.UserRepository;

@Service
public class UserProfileService {

	private final UserRepository userRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final PasswordEncoder passwordEncoder;

	public UserProfileService(
			UserRepository userRepository,
			RefreshTokenRepository refreshTokenRepository,
			PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.refreshTokenRepository = refreshTokenRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public UserResponse update(String email, UpdateProfileRequest request) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserNotFoundException(email));

		user.setFirstName(request.getFirstName().trim());
		user.setLastName(request.getLastName().trim());

		if (request.getPassword() != null && !request.getPassword().isBlank()) {
			user.setPassword(passwordEncoder.encode(request.getPassword()));
			refreshTokenRepository.deleteByUserId(user.getId());
		}

		User savedUser = userRepository.save(user);
		return new UserResponse(
				savedUser.getId(),
				savedUser.getFirstName(),
				savedUser.getLastName(),
				savedUser.getEmail(),
				savedUser.getRole(),
				savedUser.getEnabled(),
				savedUser.getCreatedAt());
	}
}
