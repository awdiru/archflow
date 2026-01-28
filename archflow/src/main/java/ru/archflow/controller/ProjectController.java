package ru.archflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.archflow.model.dto.project.CreateProjectRequest;
import ru.archflow.model.dto.project.InviteMemberRequest;
import ru.archflow.model.dto.project.UpdateMemberRoleRequest;
import ru.archflow.model.entity.enums.ProjectStatus;
import ru.archflow.model.entity.list.User;
import ru.archflow.service.api.ProjectService;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Project Management", description = "Управление проектами и участниками")
@SecurityRequirement(name = "Bearer Authentication")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @Operation(summary = "Создать новый проект")
    public ResponseEntity<?> createProject(@RequestBody CreateProjectRequest request,
                                           @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(projectService.createProject(
                request.getName(),
                request.getDescription(),
                currentUser
        ));
    }

    @GetMapping
    @Operation(summary = "Получить список всех моих проектов")
    public ResponseEntity<?> getMyProjects(@RequestParam(required = false) ProjectStatus status,
                                           @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(projectService.getUserProjects(status, currentUser.getId()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить детальную информацию о проекте")
    public ResponseEntity<?> getProject(@PathVariable Long id,
                                        @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(projectService.getProjectById(id, currentUser.getId()));
    }

    @PostMapping("/{id}/members")
    @Operation(summary = "Пригласить участника в проект по Email")
    public ResponseEntity<?> addMember(@PathVariable Long id,
                                            @RequestBody InviteMemberRequest request,
                                            @AuthenticationPrincipal User currentUser) {

        projectService.addMember(id, request, currentUser.getId());
        return ResponseEntity.ok("Member added successfully");
    }

    @PatchMapping("/{id}/members/{userId}")
    @Operation(summary = "Изменить роль участника")
    public ResponseEntity<?> updateMemberRole(@PathVariable Long id,
                                                 @PathVariable Long userId,
                                                 @RequestBody UpdateMemberRoleRequest request,
                                                 @AuthenticationPrincipal User currentUser) {

        projectService.updateMemberRole(id, userId, request.getRole(), currentUser.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/members/{userId}")
    @Operation(summary = "Удалить участника из проекта")
    public ResponseEntity<?> removeMember(@PathVariable Long id,
                                             @PathVariable Long userId,
                                             @AuthenticationPrincipal User currentUser) {

        projectService.removeMember(id, userId, currentUser.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/transfer-ownership")
    @Operation(summary = "Передать права владельца проекта другому участнику")
    public ResponseEntity<?> transferOwnership(@PathVariable Long id,
                                                  @RequestParam Long newOwnerId,
                                                  @AuthenticationPrincipal User currentUser) {

        projectService.transferOwnership(id, newOwnerId, currentUser.getId());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> setProjectStatus(@PathVariable Long id,
                                              @RequestParam ProjectStatus newStatus,
                                              @AuthenticationPrincipal User currentUser) {

        projectService.setProjectStatus(id, newStatus, currentUser.getId());
        return ResponseEntity.ok().build();
    }
}