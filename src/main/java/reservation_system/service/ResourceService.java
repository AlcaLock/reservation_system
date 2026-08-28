package reservation_system.service;


import reservation_system.dto.CreateResourceRequest;
import reservation_system.entity.Resource;
import reservation_system.entity.ResourceStatus;
import reservation_system.entity.ResourceType;
import reservation_system.repository.ResourceRepository;
import reservation_system.dto.ResourceResponse;
import reservation_system.dto.UpdateResourceRequest;
import reservation_system.dto.UpdateResourceStatusRequest;

import java.util.stream.Collectors;

import reservation_system.exception.InvalidResourceStatusException;
import reservation_system.exception.ResourceNotFoundException;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ResourceService {
    
    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

public List<ResourceResponse> findAll() {
    return findAll(null, null, null, null);
}

public List<ResourceResponse> findAll(
        String search,
        ResourceType type,
        ResourceStatus status,
        Integer minCapacity
) {
    return resourceRepository.search(
                    normalizeSearch(search),
                    type,
                    status,
                    minCapacity
            )
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
}

private String normalizeSearch(String search) {
    if (search == null || search.isBlank()) {
        return null;
    }

    return search.trim();
}

public ResourceResponse findById(Long id) {

    Resource resource = resourceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(id));

    return toResponse(resource);
}

public ResourceResponse create(CreateResourceRequest request) {
    validateCapacity(request.getCapacity());

    Resource resource = new Resource(
            request.getName(),
            request.getDescription(),
            request.getType(),
            request.getCapacity(),
            request.getLocation(),
            ResourceStatus.AVAILABLE
    );

    Resource savedResource = resourceRepository.save(resource);

    return toResponse(savedResource);
}

public ResourceResponse update(
        Long id,
        UpdateResourceRequest request
) {
    validateCapacity(request.getCapacity());

    Resource resource = resourceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(id));

    resource.setName(request.getName());
    resource.setDescription(request.getDescription());
    resource.setType(request.getType());
    resource.setCapacity(request.getCapacity());
    resource.setLocation(request.getLocation());

    Resource updatedResource = resourceRepository.save(resource);

    return toResponse(updatedResource);
}


public ResourceResponse updateStatus(
        Long id,
        UpdateResourceStatusRequest request
) {
    Resource resource = resourceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(id));

    ResourceStatus currentStatus = resource.getStatus();
    ResourceStatus newStatus = request.getStatus();

    if (currentStatus == newStatus) {
        throw new InvalidResourceStatusException(
                "Resource is already in status " + newStatus
        );
    }

    if (!isValidStatusTransition(currentStatus, newStatus)) {
        throw new InvalidResourceStatusException(
                "Cannot change resource status from "
                        + currentStatus
                        + " to "
                        + newStatus
        );
    }

    resource.setStatus(newStatus);

    Resource updatedResource = resourceRepository.save(resource);

    return toResponse(updatedResource);
}


private boolean isValidStatusTransition(
        ResourceStatus currentStatus,
        ResourceStatus newStatus
) {
    return switch (currentStatus) {

        case AVAILABLE ->
                newStatus == ResourceStatus.MAINTENANCE
                        || newStatus == ResourceStatus.INACTIVE;

        case MAINTENANCE ->
                newStatus == ResourceStatus.AVAILABLE
                        || newStatus == ResourceStatus.INACTIVE;

        case INACTIVE ->
                newStatus == ResourceStatus.AVAILABLE;
    };
}


    private ResourceResponse toResponse(Resource resource) {
    return new ResourceResponse(
            resource.getId(),
            resource.getName(),
            resource.getDescription(),
            resource.getType(),
            resource.getCapacity(),
            resource.getLocation(),
            resource.getStatus()
    );
}

private void validateCapacity(Integer capacity) {
    if (capacity == null || capacity < 1) {
        throw new IllegalArgumentException(
                "Resource capacity must be greater than zero"
        );
    }
}
   


}
