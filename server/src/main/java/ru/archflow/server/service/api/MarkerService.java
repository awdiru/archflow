package ru.archflow.server.service.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.archflow.server.model.dto.marker.CreateMarkerRequest;
import ru.archflow.server.model.dto.marker.MarkerResponse;
import ru.archflow.server.model.entity.enums.ProjectRole;
import ru.archflow.server.model.entity.list.BlueprintMarker;
import ru.archflow.server.repository.BlueprintMarkerRepository;
import ru.archflow.server.repository.BlueprintRepository;
import ru.archflow.server.repository.CatalogItemRepository;
import ru.archflow.server.service.util.UserUtilService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarkerService {

    private final BlueprintMarkerRepository markerRepository;
    private final BlueprintRepository blueprintRepository;
    private final CatalogItemRepository catalogItemRepository;
    private final ProjectService projectService;
    private final UserUtilService userUtilService;

    @Transactional
    public MarkerResponse createMarker(Long projectId, Long blueprintId, CreateMarkerRequest request, Long userId) {
        userUtilService.validateProjectRoleAccess(projectId, userId, ProjectRole.OWNER, ProjectRole.EDITOR, ProjectRole.CONTRIBUTOR);

        BlueprintMarker marker = BlueprintMarker.builder()
                .blueprint(blueprintRepository.getReferenceById(blueprintId))
                .catalogItem(catalogItemRepository.getReferenceById(request.getCatalogItemId()))
                .quantity(request.getQuantity())
                .coordinates(request.getCoordinates())
                .build();

        MarkerResponse response = mapToResponse(markerRepository.save(marker));
        projectService.updateProjectBudget(projectId);
        return response;
    }

    @Transactional(readOnly = true)
    public List<MarkerResponse> getMarkersByBlueprint(Long projectId, Long blueprintId, Long userId) {
        userUtilService.validateProjectRoleAccess(projectId, userId, ProjectRole.values());
        return markerRepository.findAllByBlueprintId(blueprintId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public void deleteMarker(Long projectId, Long markerId, Long userId) {
        userUtilService.validateProjectRoleAccess(projectId, userId, ProjectRole.OWNER, ProjectRole.EDITOR, ProjectRole.CONTRIBUTOR);
        markerRepository.deleteById(markerId);
        projectService.updateProjectBudget(projectId);
    }

    private MarkerResponse mapToResponse(BlueprintMarker marker) {
        return MarkerResponse.builder()
                .id(marker.getId())
                .blueprintId(marker.getBlueprint().getId())
                .catalogItemId(marker.getCatalogItem().getId())
                .catalogItemName(marker.getCatalogItem().getName())
                .quantity(marker.getQuantity())
                .coordinates(marker.getCoordinates())
                .build();
    }
}