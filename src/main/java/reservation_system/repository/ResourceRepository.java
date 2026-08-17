package reservation_system.repository;
import reservation_system.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ResourceRepository extends JpaRepository<Resource, Long> {



}
