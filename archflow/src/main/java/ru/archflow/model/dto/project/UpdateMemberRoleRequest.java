package ru.archflow.model.dto.project;

import lombok.Data;
import ru.archflow.model.entity.enums.ProjectRole;

@Data
public class UpdateMemberRoleRequest {
    private ProjectRole role;
}