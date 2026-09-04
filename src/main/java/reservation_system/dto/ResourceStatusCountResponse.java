package reservation_system.dto;

import reservation_system.entity.ResourceStatus;

public record ResourceStatusCountResponse(ResourceStatus status, long resources) {
}