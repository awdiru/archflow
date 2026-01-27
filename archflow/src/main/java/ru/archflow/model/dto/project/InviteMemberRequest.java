package ru.archflow.model.dto.project;

import lombok.Data;
import ru.archflow.model.entity.enums.ProjectRole;

@Data
public class InviteMemberRequest {
    private String email;
    private ProjectRole role;
}