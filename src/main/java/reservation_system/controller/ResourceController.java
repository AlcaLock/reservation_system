package reservation_system.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import jakarta.validation.Valid;
import reservation_system.dto.CreateResourceRequest;
import reservation_system.dto.ResourceResponse;
import reservation_system.dto.UpdateResourceRequest;
import reservation_system.dto.UpdateResourceStatusRequest;

import reservation_system.service.ResourceService;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping
    public List<ResourceResponse> findAll() {
        return resourceService.findAll();
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