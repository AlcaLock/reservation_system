package reservation_system.repository;
import reservation_system.entity.Resource;
import reservation_system.entity.ResourceStatus;
import reservation_system.entity.ResourceType;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;


public interface ResourceRepository extends JpaRepository<Resource, Long> {

@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("select r from Resource r where r.id = :id")
Optional<Resource> findByIdForUpdate(@Param("id") Long id);


    @Query("""
            SELECT resource
            FROM Resource resource
            WHERE (:search IS NULL
                OR LOWER(resource.name) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(resource.description) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:type IS NULL OR resource.type = :type)
              AND (:status IS NULL OR resource.status = :status)
              AND (:minCapacity IS NULL OR resource.capacity >= :minCapacity)
            ORDER BY resource.name ASC
            """)
    List<Resource> search(
            @Param("search") String search,
            @Param("type") ResourceType type,
            @Param("status") ResourceStatus status,
            @Param("minCapacity") Integer minCapacity
    );

}
