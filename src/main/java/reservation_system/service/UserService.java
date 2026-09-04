package reservation_system.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import reservation_system.dto.UpdateUserRoleRequest;
import reservation_system.dto.UpdateUserStatusRequest;
import reservation_system.dto.UserResponse;
import reservation_system.entity.User;
import reservation_system.exception.UserNotFoundException;
import reservation_system.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional(readOnly = true)
	public List<UserResponse> findAll(String search) {
		String normalizedSearch = search == null || search.isBlank() ? null : search.trim();
		return userRepository.search(normalizedSearch).stream().map(this::toResponse).toList();
	}

	@Transactional(readOnly = true)
	public UserResponse findById(Long id) {
		return toResponse(findUser(id));
	}

	@Transactional(readOnly = true)
	public UserResponse findByEmail(String email) {
		return userRepository.findByEmail(email)
				.map(this::toResponse)
				.orElseThrow(() -> new UserNotFoundException(email));
	}

	@Transactional
	public UserResponse updateRole(Long id, UpdateUserRoleRequest request) {
		User user = findUser(id);
		user.setRole(request.getRole());
		return toResponse(userRepository.save(user));
	}

	@Transactional
	public UserResponse updateStatus(Long id, UpdateUserStatusRequest request) {
		User user = findUser(id);
		user.setEnabled(request.getEnabled());
		return toResponse(userRepository.save(user));
	}

	private User findUser(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
	}

	private UserResponse toResponse(User user) {
		return new UserResponse(
				user.getId(),
				user.getFirstName(),
				user.getLastName(),
				user.getEmail(),
				user.getRole(),
				user.getEnabled(),
				user.getCreatedAt());
	}
}
