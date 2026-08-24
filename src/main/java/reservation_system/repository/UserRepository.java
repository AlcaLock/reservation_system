package reservation_system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import reservation_system.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
