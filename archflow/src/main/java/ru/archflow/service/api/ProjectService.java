package ru.archflow.service.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.archflow.model.dto.project.InviteMemberRequest;
import ru.archflow.model.dto.project.ProjectDetailsResponse;
import ru.archflow.model.dto.project.ProjectMemberResponse;
import ru.archflow.model.dto.project.ProjectResponse;
import ru.archflow.model.entity.enums.ProjectRole;
import ru.archflow.model.entity.enums.ProjectStatus;
import ru.archflow.model.entity.list.Project;
import ru.archflow.model.entity.list.ProjectMember;
import ru.archflow.model.entity.list.User;
import ru.archflow.repository.BlueprintMarkerRepository;
import ru.archflow.repository.ProjectMemberRepository;
import ru.archflow.repository.ProjectRepository;
import ru.archflow.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository memberRepository;
    private final BlueprintMarkerRepository markerRepository;

    @Transactional
    public ProjectResponse createProject(String name, String description, User creator) {
        Project project = Project.builder()
                .name(name)
                .description(description)
                .status(ProjectStatus.DRAFT)
                .owner(creator)
                .build();

        Project savedProject = projectRepository.save(project);

        ProjectMember ownerMember = ProjectMember.builder()
                .project(savedProject)
                .user(creator)
                .role(ProjectRole.OWNER)
                .build();

        memberRepository.save(ownerMember);

        return mapToResponse(savedProject);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getUserProjects(ProjectStatus status, Long userId) {
        if (status == null)
            return projectRepository.findAllByUserId(userId).stream()
                    .map(this::mapToResponse)
                    .toList();
        return projectRepository.findAllByUserIdAndStatus(userId, status)
                .stream().map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectDetailsResponse getProjectById(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        validateAccess(projectId, userId);

        return mapToDetailsResponse(project);
    }

    @Transactional
    public void addMember(Long projectId, InviteMemberRequest request, Long adminId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        ProjectMember manager = memberRepository.findByProjectIdAndUserId(projectId, adminId)
                .orElseThrow(() -> new RuntimeException("Access denied"));

        if (manager.getRole() != ProjectRole.OWNER && manager.getRole() != ProjectRole.EDITOR)
            throw new RuntimeException("You don't have permission to invite members");

        User userToInvite = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User with this email not found"));

        if (memberRepository.findByProjectIdAndUserId(projectId, userToInvite.getId()).isPresent())
            throw new RuntimeException("User is already a member of this project");

        ProjectMember newMember = ProjectMember.builder()
                .project(project)
                .user(userToInvite)
                .role(manager.getRole() == ProjectRole.OWNER ? request.getRole() : ProjectRole.CONTRIBUTOR)
                .build();

        memberRepository.save(newMember);
    }

    @Transactional
    public void updateMemberRole(Long projectId, Long targetUserId, ProjectRole newRole, Long adminId) {
        validateRoleAccess(projectId, adminId, ProjectRole.OWNER);

        ProjectMember member = memberRepository.findByProjectIdAndUserId(projectId, targetUserId)
                .orElseThrow(() -> new RuntimeException("Member not found in this project"));

        if (member.getRole() == ProjectRole.OWNER)
            throw new RuntimeException("Cannot change role of the project owner");

        member.setRole(newRole);
        memberRepository.save(member);
    }

    @Transactional
    public void removeMember(Long projectId, Long targetUserId, Long adminId) {
        validateRoleAccess(projectId, adminId, ProjectRole.OWNER);

        ProjectMember member = memberRepository.findByProjectIdAndUserId(projectId, targetUserId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        if (member.getRole() == ProjectRole.OWNER)
            throw new RuntimeException("Cannot remove the owner from the project");

        memberRepository.delete(member);
    }

    @Transactional
    public void transferOwnership(Long projectId, Long newOwnerId, Long currentOwnerId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOwner().getId().equals(currentOwnerId))
            throw new RuntimeException("Only the current owner can transfer ownership");

        ProjectMember newOwnerMember = memberRepository.findByProjectIdAndUserId(projectId, newOwnerId)
                .orElseThrow(() -> new RuntimeException("New owner must be a member of the project"));

        User newOwner = newOwnerMember.getUser();
        project.setOwner(newOwner);
        projectRepository.save(project);

        ProjectMember oldOwnerMember = memberRepository.findByProjectIdAndUserId(projectId, currentOwnerId)
                .orElseThrow(() -> new RuntimeException("Internal error: owner member not found"));

        oldOwnerMember.setRole(ProjectRole.EDITOR);
        newOwnerMember.setRole(ProjectRole.OWNER);

        memberRepository.saveAll(List.of(oldOwnerMember, newOwnerMember));
    }

    @Transactional
    public void setProjectStatus(Long projectId, ProjectStatus newStatus, Long adminId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        if (!project.getOwner().getId().equals(adminId))
            throw new RuntimeException("Only the current owner can transfer ownership");

        project.setStatus(newStatus);
        projectRepository.save(project);
    }

    @Transactional
    public void updateProjectBudget(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        BigDecimal total = markerRepository.calculateTotalBudgetByProjectId(projectId);

        // Если меток нет, сумма будет null, превращаем в 0
        project.setTotalBudget(total != null ? total : BigDecimal.ZERO);
        projectRepository.save(project);
    }

    public void validateRoleAccess(Long projectId, Long userId, ProjectRole ... roles) {
        ProjectMember admin = memberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new RuntimeException("Access denied"));

        for (ProjectRole role : roles) {
            if (admin.getRole().equals(role))
                return;
        }
        throw new RuntimeException("Access denied");
    }

    private void validateAccess(Long projectId, Long userId) {
        if (memberRepository.findByProjectIdAndUserId(projectId, userId).isEmpty())
            throw new RuntimeException("Access denied: You are not a member of this project");
    }

    private ProjectResponse mapToResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .address(project.getAddress())
                .status(project.getStatus())
                .totalBudget(project.getTotalBudget())
                .ownerId(project.getOwner().getId())
                .ownerFullName(project.getOwner().getFullName())
                .createdAt(project.getCreatedAt())
                .build();
    }

    private ProjectDetailsResponse mapToDetailsResponse(Project project) {
        List<ProjectMemberResponse> memberDtos = project.getMembers().stream()
                .map(m -> ProjectMemberResponse.builder()
                        .userId(m.getUser().getId())
                        .fullName(m.getUser().getFullName())
                        .email(m.getUser().getEmail())
                        .projectRole(m.getRole())
                        .build())
                .collect(Collectors.toList());

        return ProjectDetailsResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .address(project.getAddress())
                .status(project.getStatus())
                .totalBudget(project.getTotalBudget())
                .createdAt(project.getCreatedAt())
                .ownerId(project.getOwner().getId())
                .ownerFullName(project.getOwner().getFullName())
                .members(memberDtos) // Добавляем список участников
                .build();
    }
}