package reservation_system;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import reservation_system.dto.CreateReservationRequest;
import reservation_system.entity.ReservationStatus;
import reservation_system.entity.Resource;
import reservation_system.entity.ResourceStatus;
import reservation_system.entity.ResourceType;
import reservation_system.entity.User;
import reservation_system.entity.UserRole;
import reservation_system.exception.ReservationConflictException;
import reservation_system.repository.ReservationRepository;
import reservation_system.repository.ResourceRepository;
import reservation_system.repository.UserRepository;
import reservation_system.service.ReservationService;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class ReservationConcurrencyIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void cleanDatabase() {
        reservationRepository.deleteAll();
        resourceRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldAllowOnlyOneConcurrentReservationForTheSameResourceAndInterval()
            throws InterruptedException {
        Resource resource = resourceRepository.save(new Resource(
                "Sala 101", "Sala de estudio", ResourceType.ROOM, 30,
                "Edificio A", ResourceStatus.AVAILABLE
        ));
        User firstUser = userRepository.save(new User(
                "Ana", "Lopez", "ana@example.com", "temporary", UserRole.STUDENT
        ));
        User secondUser = userRepository.save(new User(
                "Luis", "Perez", "luis@example.com", "temporary", UserRole.STUDENT
        ));
        LocalDateTime startTime = LocalDateTime.now().plusDays(1).withNano(0);
        LocalDateTime endTime = startTime.plusHours(2);

        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            List<Future<?>> attempts = List.of(
                    executor.submit(() -> createReservation(firstUser.getId(), resource.getId(), startTime, endTime, ready, start)),
                    executor.submit(() -> createReservation(secondUser.getId(), resource.getId(), startTime, endTime, ready, start))
            );

            assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();
            start.countDown();

            int successfulReservations = 0;
            int conflicts = 0;
            for (Future<?> attempt : attempts) {
                try {
                    attempt.get(10, TimeUnit.SECONDS);
                    successfulReservations++;
                } catch (ExecutionException exception) {
                    assertThat(exception.getCause()).isInstanceOf(ReservationConflictException.class);
                    conflicts++;
                } catch (java.util.concurrent.TimeoutException exception) {
                    throw new AssertionError("Concurrent reservation attempt timed out.", exception);
                }
            }

            assertThat(successfulReservations).isEqualTo(1);
            assertThat(conflicts).isEqualTo(1);
            assertThat(reservationRepository.count()).isEqualTo(1);
            assertThat(reservationRepository.findAll().getFirst().getStatus())
                    .isEqualTo(ReservationStatus.ACTIVE);
        } finally {
            executor.shutdownNow();
        }
    }

    private void createReservation(
            Long userId,
            Long resourceId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            CountDownLatch ready,
            CountDownLatch start
    ) {
        ready.countDown();
        try {
            if (!start.await(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Concurrent test did not start in time.");
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Concurrent test was interrupted.", exception);
        }

        CreateReservationRequest request = new CreateReservationRequest();
        request.setUserId(userId);
        request.setResourceId(resourceId);
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setPurpose("Concurrent reservation test");
        reservationService.create(request);
    }
}