package reservation_system.service;

import org.springframework.stereotype.Service;
import reservation_system.entity.Resource;
import reservation_system.repository.ResourceRepository;

import java.util.List;

@Service
public class ResourceService {
    
    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    public Resource getResourceById(Long id) {
        return resourceRepository.findById(id).orElse(null);
    }

    public Resource createResource(Resource resource) {
        return resourceRepository.save(resource);
    }

    public Resource updateResource(Long id, Resource updatedResource) {
        return resourceRepository.findById(id)
                .map(resource -> {
                    resource.setName(updatedResource.getName());
                    resource.setDescription(updatedResource.getDescription());
                    resource.setType(updatedResource.getType());
                    resource.setCapacity(updatedResource.getCapacity());
                    resource.setLocation(updatedResource.getLocation());
                    resource.setStatus(updatedResource.getStatus());
                    return resourceRepository.save(resource);
                })
                .orElse(null);
    }

    public void deleteResource(Long id) {
        resourceRepository.deleteById(id);
    }
}
