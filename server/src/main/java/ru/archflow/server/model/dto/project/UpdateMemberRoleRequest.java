package ru.archflow.server.model.dto.project;

import lombok.Data;
import ru.archflow.server.model.entity.enums.ProjectRole;

@Data
public class UpdateMemberRoleRequest {
    private ProjectRole role;
}