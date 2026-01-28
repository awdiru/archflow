package ru.archflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.archflow.model.dto.marker.CreateMarkerRequest;
import ru.archflow.model.entity.list.User;
import ru.archflow.service.api.MarkerService;

@RestController
@RequestMapping("/api/projects/{projectId}/blueprints/{blueprintId}/markers")
@RequiredArgsConstructor
@Tag(name = "Markers", description = "Управление интерактивными метками на чертежах")
public class MarkerController {

    private final MarkerService markerService;

    @PostMapping
    @Operation(summary = "Добавить метку на чертеж")
    public ResponseEntity<?> create(@PathVariable Long projectId,
                                    @PathVariable Long blueprintId,
                                    @RequestBody CreateMarkerRequest request,
                                    @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(markerService.createMarker(projectId, blueprintId, request, currentUser.getId()));
    }

    @GetMapping
    @Operation(summary = "Получить все метки чертежа")
    public ResponseEntity<?> getList(@PathVariable Long projectId,
                                     @PathVariable Long blueprintId,
                                     @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(markerService.getMarkersByBlueprint(projectId, blueprintId, currentUser.getId()));
    }

    @DeleteMapping("/{markerId}")
    @Operation(summary = "Удалить метку")
    public ResponseEntity<?> delete(@PathVariable Long projectId,
                                    @PathVariable Long markerId,
                                    @AuthenticationPrincipal User currentUser) {

        markerService.deleteMarker(projectId, markerId, currentUser.getId());
        return ResponseEntity.ok().build();
    }
}