package reservation_system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import reservation_system.dto.CreateResourceRequest;
import reservation_system.dto.ResourceResponse;
import reservation_system.dto.UpdateResourceRequest;
import reservation_system.dto.UpdateResourceStatusRequest;
import reservation_system.entity.Resource;
import reservation_system.entity.ResourceStatus;
import reservation_system.entity.ResourceType;
import reservation_system.exception.InvalidResourceStatusException;
import reservation_system.exception.ResourceNotFoundException;
import reservation_system.repository.ResourceRepository;
import reservation_system.service.ResourceService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;


public class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    private ResourceService resourceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resourceService = new ResourceService(resourceRepository);
    }

    @Test
    void shouldCreateResourceWithAvailableStatus() {
        CreateResourceRequest request = new CreateResourceRequest();

        request.setName("Sala 101");
        request.setDescription("Sala de estudio");
        request.setType(ResourceType.ROOM);
        request.setCapacity(30);
        request.setLocation("Edificio A");

        Resource savedResource = new Resource(
                "Sala 101",
                "Sala de estudio",
                ResourceType.ROOM,
                30,
                "Edificio A",
                ResourceStatus.AVAILABLE);

        when(resourceRepository.save(org.mockito.ArgumentMatchers.any(Resource.class)))
                .thenReturn(savedResource);
        ResourceResponse response = resourceService.create(request);

        assertThat(response.getName()).isEqualTo("Sala 101");
        assertThat(response.getType()).isEqualTo(ResourceType.ROOM);
        assertThat(response.getCapacity()).isEqualTo(30);
        assertThat(response.getStatus()).isEqualTo(ResourceStatus.AVAILABLE);

        verify(resourceRepository).save(org.mockito.ArgumentMatchers.any(Resource.class));
    }

@Test
void shouldThrowExceptionWhenResourceDoesNotExist() {

    Long resourceId = 999L;

    when(resourceRepository.findById(resourceId))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() -> resourceService.findById(resourceId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Resource with id 999 not found.");
}

    @Test
    void shouldUpdateResource() {

        Long resourceId = 1L;

        Resource existingResource = new Resource(
                "Sala 101",
                "Sala antigua",
                ResourceType.ROOM,
                20,
                "Edificio A",
                ResourceStatus.AVAILABLE);

        UpdateResourceRequest request = new UpdateResourceRequest();

        request.setName("Sala 101 Renovada");
        request.setDescription("Sala renovada");
        request.setType(ResourceType.ROOM);
        request.setCapacity(35);
        request.setLocation("Edificio B");

        when(resourceRepository.findById(resourceId))
                .thenReturn(Optional.of(existingResource));

        when(resourceRepository.save(existingResource))
                .thenReturn(existingResource);

        ResourceResponse response = resourceService.update(resourceId, request);

        assertThat(response.getName())
                .isEqualTo("Sala 101 Renovada");

        assertThat(response.getCapacity())
                .isEqualTo(35);

        assertThat(response.getLocation())
                .isEqualTo("Edificio B");

        assertThat(response.getStatus())
                .isEqualTo(ResourceStatus.AVAILABLE);

        verify(resourceRepository).save(existingResource);
    }

    @Test
void shouldChangeResourceFromAvailableToMaintenance() {
    Long resourceId = 1L;

    Resource resource = new Resource(
            "Sala 101",
            "Sala de estudio",
            ResourceType.ROOM,
            30,
            "Edificio A",
            ResourceStatus.AVAILABLE
    );

    UpdateResourceStatusRequest request =
            new UpdateResourceStatusRequest();

    request.setStatus(ResourceStatus.MAINTENANCE);

    when(resourceRepository.findById(resourceId))
            .thenReturn(Optional.of(resource));

    when(resourceRepository.save(resource))
            .thenReturn(resource);

    ResourceResponse response =
            resourceService.updateStatus(resourceId, request);

    assertThat(response.getStatus())
            .isEqualTo(ResourceStatus.MAINTENANCE);

    verify(resourceRepository).save(resource);
}

@Test
void shouldRejectSameResourceStatus() {
    Long resourceId = 1L;

    Resource resource = new Resource(
            "Sala 101",
            "Sala de estudio",
            ResourceType.ROOM,
            30,
            "Edificio A",
            ResourceStatus.AVAILABLE
    );

    UpdateResourceStatusRequest request =
            new UpdateResourceStatusRequest();

    request.setStatus(ResourceStatus.AVAILABLE);

    when(resourceRepository.findById(resourceId))
            .thenReturn(Optional.of(resource));

    assertThatThrownBy(() ->
            resourceService.updateStatus(resourceId, request)
    )
            .isInstanceOf(InvalidResourceStatusException.class)
            .hasMessage("Resource is already in status AVAILABLE");

    verify(resourceRepository, never()).save(resource);
}

@Test
void shouldRejectInvalidResourceStatusTransition() {
    Long resourceId = 1L;

    Resource resource = new Resource(
            "Sala 101",
            "Sala de estudio",
            ResourceType.ROOM,
            30,
            "Edificio A",
            ResourceStatus.INACTIVE
    );

    UpdateResourceStatusRequest request =
            new UpdateResourceStatusRequest();

    request.setStatus(ResourceStatus.MAINTENANCE);

    when(resourceRepository.findById(resourceId))
            .thenReturn(Optional.of(resource));

    assertThatThrownBy(() ->
            resourceService.updateStatus(resourceId, request)
    )
            .isInstanceOf(InvalidResourceStatusException.class)
            .hasMessage(
                    "Cannot change resource status from INACTIVE to MAINTENANCE"
            );

    verify(resourceRepository, never()).save(resource);
}


@Test
void shouldFilterResourcesBySearchAndAvailability() {
    Resource resource = new Resource(
            "Sala 101",
            "Sala de estudio",
            ResourceType.ROOM,
            30,
            "Edificio A",
            ResourceStatus.AVAILABLE
    );

    when(resourceRepository.search(
            "sala",
            ResourceType.ROOM,
            ResourceStatus.AVAILABLE,
            20
    )).thenReturn(List.of(resource));

    List<ResourceResponse> response = resourceService.findAll(
            " sala ",
            ResourceType.ROOM,
            ResourceStatus.AVAILABLE,
            20
    );

    assertThat(response)
            .hasSize(1)
            .first()
            .satisfies(result -> {
                assertThat(result.getName())
                        .isEqualTo("Sala 101");
                assertThat(result.getStatus())
                        .isEqualTo(ResourceStatus.AVAILABLE);
            });

    verify(resourceRepository).search(
            "sala",
            ResourceType.ROOM,
            ResourceStatus.AVAILABLE,
            20
    );
}

@Test
void shouldReturnAllResourcesWithoutFilters() {
    Resource resource = new Resource(
            "Sala 101",
            "Sala de estudio",
            ResourceType.ROOM,
            30,
            "Edificio A",
            ResourceStatus.AVAILABLE
    );

    when(resourceRepository.search(null, null, null, null))
            .thenReturn(List.of(resource));

    List<ResourceResponse> response = resourceService.findAll();

    assertThat(response)
            .hasSize(1)
            .first()
            .extracting(ResourceResponse::getName)
            .isEqualTo("Sala 101");

    verify(resourceRepository)
            .search(null, null, null, null);
}

    
}
