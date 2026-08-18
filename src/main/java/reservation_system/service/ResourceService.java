package reservation_system.service;

import jakarta.validation.Valid;
import reservation_system.dto.CreateResourceRequest;
import reservation_system.entity.Resource;
import reservation_system.entity.ResourceStatus;
import reservation_system.repository.ResourceRepository;
import reservation_system.dto.ResourceResponse;
import java.util.stream.Collectors;
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
    return resourceRepository.findAll()
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
}

public ResourceResponse findById(Long id) {

    Resource resource = resourceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(id));

    return toResponse(resource);
}

        public ResourceResponse create(CreateResourceRequest request) {
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
   
}
