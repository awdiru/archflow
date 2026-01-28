package ru.archflow.service.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.archflow.model.dto.blueprint.BlueprintResponse;
import ru.archflow.model.entity.enums.BlueprintType;
import ru.archflow.model.entity.enums.ProjectRole;
import ru.archflow.model.entity.list.Blueprint;
import ru.archflow.repository.BlueprintRepository;
import ru.archflow.repository.ProjectRepository;
import ru.archflow.service.util.MinioService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlueprintService {
    private final MinioService minioService;
    private final BlueprintRepository blueprintRepository;
    private final ProjectRepository projectRepository;
    private final ProjectService projectService;

    @Transactional
    public void uploadBlueprint(Long projectId, String name, BlueprintType type,
                                String changeLog, MultipartFile file, Long userId) {

        projectService.validateRoleAccess(projectId, userId, ProjectRole.OWNER, ProjectRole.EDITOR);

        String newName = name == null || name.isEmpty() ? file.getOriginalFilename() : name;

        Integer lastVersion = blueprintRepository
                .findFirstByProjectIdAndNameAndTypeOrderByVersionDesc(projectId, newName, type)
                .map(Blueprint::getVersion)
                .orElse(0);

        int newVersion = lastVersion + 1;
        String fileName = String.format("projects/%d/%s/v%d_%s",
                projectId, type.name(), newVersion, newName.replace(" ", "_"));

        minioService.uploadFile(file, fileName);

        Blueprint blueprint = Blueprint.builder()
                .project(projectRepository.getReferenceById(projectId))
                .name(newName)
                .type(type)
                .fileUrl(fileName)
                .version(newVersion)
                .changeLog(changeLog)
                .isApproved(false)
                .build();

        blueprintRepository.save(blueprint);
    }

    @Transactional
    public void approveBlueprint(Long projectId, Long blueprintId, Long userId) {
        projectService.validateRoleAccess(projectId, userId, ProjectRole.OWNER); // Только владелец одобряет

        Blueprint target = blueprintRepository.findById(blueprintId)
                .orElseThrow(() -> new RuntimeException("Blueprint not found"));

        // Сбрасываем статус Approved у всех версий этого конкретного чертежа (имя + тип)
        List<Blueprint> allVersions = blueprintRepository
                .findAllByProjectIdAndNameAndTypeOrderByVersionDesc(projectId, target.getName(), target.getType());

        allVersions.forEach(b -> b.setIsApproved(false));

        // Одобряем конкретную выбранную версию
        target.setIsApproved(true);
        blueprintRepository.saveAll(allVersions);
    }

    @Transactional(readOnly = true)
    public List<BlueprintResponse> getProjectBlueprints(Long projectId, Long userId) {
        projectService.validateRoleAccess(projectId, userId, ProjectRole.values());

        return blueprintRepository.findAllLatestByProjectId(projectId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BlueprintResponse> getVersionHistory(Long projectId, String name, BlueprintType type, Long userId) {
        projectService.validateRoleAccess(projectId, userId, ProjectRole.values());
        return blueprintRepository.findAllByProjectIdAndNameAndTypeOrderByVersionDesc(projectId, name, type).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private BlueprintResponse mapToResponse(Blueprint blueprint) {
        return BlueprintResponse.builder()
                .id(blueprint.getId())
                .name(blueprint.getName())
                .type(blueprint.getType())
                .fileUrl(blueprint.getFileUrl())
                .version(blueprint.getVersion())
                .isApproved(blueprint.getIsApproved())
                .changeLog(blueprint.getChangeLog())
                .createdAt(blueprint.getCreatedAt())
                .build();
    }
}