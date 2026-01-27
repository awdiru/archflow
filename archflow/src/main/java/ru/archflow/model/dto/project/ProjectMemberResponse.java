package ru.archflow.model.dto.project;

import lombok.Builder;
import lombok.Data;
import ru.archflow.model.entity.enums.ProjectRole;

@Data
@Builder
public class ProjectMemberResponse {
    private Long userId;
    private String fullName;
    private String email;
    private ProjectRole projectRole;
}