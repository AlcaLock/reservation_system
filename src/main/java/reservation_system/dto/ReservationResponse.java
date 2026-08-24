package reservation_system.dto;

import java.time.LocalDateTime;

import reservation_system.entity.ReservationStatus;

public class ReservationResponse {
    
    private Long id;
    private Long userId;
    private Long resourceId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String purpose;
    private ReservationStatus status;

    public ReservationResponse(
    Long id,
    Long userId,
    Long resourceId,
    LocalDateTime startTime,
    java.time.LocalDateTime endTime,
    String purpose,
    ReservationStatus status
){
        this.id = id;
        this.userId = userId;
        this.resourceId = resourceId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.purpose = purpose;
        this.status = status;
    }
    
        public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getResourceId() { return resourceId; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public String getPurpose() { return purpose; }
    public ReservationStatus getStatus() { return status; }

}
