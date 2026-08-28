package reservation_system.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;

@Entity
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    private ResourceType type;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false, length = 150)
    private String location;

    @Enumerated(EnumType.STRING)
    private ResourceStatus status;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "resource")
    private List<Reservation> reservations = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = ResourceStatus.AVAILABLE;
        }

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Resource() {
    }

     public Resource(
            String name,
            String description,
            ResourceType type,
            Integer capacity,
            String location,
            ResourceStatus status
    ) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.capacity = capacity;
        this.location = location;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ResourceStatus getStatus() {
        return status;
    }

    public void setStatus(ResourceStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }
}