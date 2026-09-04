package reservation_system.config;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import reservation_system.entity.Reservation;
import reservation_system.entity.ReservationStatus;
import reservation_system.entity.Resource;
import reservation_system.entity.ResourceStatus;
import reservation_system.entity.ResourceType;
import reservation_system.entity.User;
import reservation_system.entity.UserRole;
import reservation_system.repository.ReservationRepository;
import reservation_system.repository.ResourceRepository;
import reservation_system.repository.UserRepository;

@Configuration
public class DemoDataInitializer {

    @Bean
    @ConditionalOnProperty(name = "app.demo-data.enabled", havingValue = "true")
    CommandLineRunner loadDemoData(
            UserRepository userRepository,
            ResourceRepository resourceRepository,
            ReservationRepository reservationRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            User student = userRepository.findByEmail("student@reservehub.demo")
                    .orElseGet(() -> userRepository.save(new User(
                            "Sofia",
                            "Martinez",
                            "student@reservehub.demo",
                            passwordEncoder.encode("DemoPass123"),
                            UserRole.STUDENT)));

            userRepository.findByEmail("admin@reservehub.demo")
                    .orElseGet(() -> userRepository.save(new User(
                            "Daniel",
                            "Ruiz",
                            "admin@reservehub.demo",
                            passwordEncoder.encode("DemoPass123"),
                            UserRole.ADMIN)));

            Resource salaInnovacion = findOrCreateResource(
                    resourceRepository,
                    "Sala de Innovacion",
                    "Espacio flexible con pantalla, pizarra y videoconferencia.",
                    ResourceType.ROOM,
                    18,
                    "Edificio Central, piso 2",
                    ResourceStatus.AVAILABLE);

            Resource laboratorioDatos = findOrCreateResource(
                    resourceRepository,
                    "Laboratorio de Datos",
                    "Estaciones de trabajo para analisis, desarrollo y practicas.",
                    ResourceType.LABORATORY,
                    24,
                    "Edificio Tecnologico, piso 1",
                    ResourceStatus.AVAILABLE);

            findOrCreateResource(
                    resourceRepository,
                    "Sala Norte",
                    "Sala silenciosa para reuniones de equipo.",
                    ResourceType.ROOM,
                    10,
                    "Edificio Norte, piso 3",
                    ResourceStatus.AVAILABLE);

            findOrCreateResource(
                    resourceRepository,
                    "Proyector Epson 4K",
                    "Proyector portatil con conexion HDMI y USB-C.",
                    ResourceType.EQUIPMENT,
                    1,
                    "Prestamos, planta baja",
                    ResourceStatus.AVAILABLE);

            findOrCreateResource(
                    resourceRepository,
                    "Laboratorio de Electronica",
                    "Bancos de trabajo para prototipado y medicion.",
                    ResourceType.LABORATORY,
                    16,
                    "Edificio Tecnologico, piso 2",
                    ResourceStatus.MAINTENANCE);

            findOrCreateResource(
                    resourceRepository,
                    "Sala Ejecutiva",
                    "Sala para presentaciones y reuniones de direccion.",
                    ResourceType.ROOM,
                    12,
                    "Edificio Central, piso 4",
                    ResourceStatus.AVAILABLE);

            findOrCreateResource(
                    resourceRepository,
                    "Kit de Grabacion",
                    "Camara, microfono y tripode para produccion audiovisual.",
                    ResourceType.EQUIPMENT,
                    1,
                    "Prestamos, planta baja",
                    ResourceStatus.AVAILABLE);

            findOrCreateResource(
                    resourceRepository,
                    "Sala Sur",
                    "Espacio actualmente fuera de servicio por remodelacion.",
                    ResourceType.ROOM,
                    20,
                    "Edificio Sur, piso 1",
                    ResourceStatus.INACTIVE);

            createReservationIfMissing(
                    reservationRepository,
                    student,
                    salaInnovacion,
                    LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0),
                    "Sesion de diseno de producto");
            createReservationIfMissing(
                    reservationRepository,
                    student,
                    laboratorioDatos,
                    LocalDateTime.now().plusDays(2).withHour(14).withMinute(0).withSecond(0).withNano(0),
                    "Taller de analisis de datos");
        };
    }

    private Resource findOrCreateResource(
            ResourceRepository resourceRepository,
            String name,
            String description,
            ResourceType type,
            int capacity,
            String location,
            ResourceStatus status) {
        return resourceRepository.search(name, null, null, null).stream()
                .filter(resource -> resource.getName().equals(name))
                .findFirst()
                .orElseGet(() -> resourceRepository.save(new Resource(
                        name, description, type, capacity, location, status)));
    }

    private void createReservationIfMissing(
            ReservationRepository reservationRepository,
            User user,
            Resource resource,
            LocalDateTime startTime,
            String purpose) {
        boolean exists = reservationRepository
                .existsByResourceAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                        resource,
                        ReservationStatus.ACTIVE,
                        startTime.plusHours(2),
                        startTime);
        if (!exists) {
            reservationRepository.save(new Reservation(
                    startTime,
                    startTime.plusHours(2),
                    purpose,
                    ReservationStatus.ACTIVE,
                    user,
                    resource));
        }
    }
}