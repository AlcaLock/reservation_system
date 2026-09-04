package reservation_system.dto;

import reservation_system.entity.ResourceType;

public record ResourceTypeCountResponse(ResourceType type, long resources) {
}