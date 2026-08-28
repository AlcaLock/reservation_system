package reservation_system.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import reservation_system.dto.CreateResourceRequest;
import reservation_system.dto.ResourceResponse;
import reservation_system.dto.UpdateResourceRequest;
import reservation_system.dto.UpdateResourceStatusRequest;
import reservation_system.entity.ResourceStatus;
import reservation_system.entity.ResourceType;
import reservation_system.service.ResourceService;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

@GetMapping
public List<ResourceResponse> findAll(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) ResourceType type,
        @RequestParam(required = false) ResourceStatus status,
        @RequestParam(required = false)
        @Min(value = 1, message = "Minimum capacity must be at least 1")
        Integer minCapacity
) {
    return resourceService.findAll(
            search,
            type,
            status,
            minCapacity
    );
}

    @GetMapping("/{id}")
public ResourceResponse findById(@PathVariable Long id) {
    return resourceService.findById(id);
}

    @PostMapping
    public ResponseEntity<ResourceResponse> create(
            @Valid @RequestBody CreateResourceRequest request) {
        ResourceResponse resource = resourceService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resource);
    }


@PutMapping("/{id}")
public ResourceResponse update(
        @PathVariable Long id,
        @Valid @RequestBody UpdateResourceRequest request
) {
    return resourceService.update(id, request);
}


@PatchMapping("/{id}/status")
public ResourceResponse updateStatus(
        @PathVariable Long id,
        @Valid @RequestBody UpdateResourceStatusRequest request
) {
    return resourceService.updateStatus(id, request);

}

}