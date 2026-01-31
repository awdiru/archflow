package ru.archflow.server.model.dto.user;

import lombok.Data;
import ru.archflow.server.model.entity.enums.Role;

@Data
public class UserCreateRequest {
    private String email;
    private String fullName;
    private Role role;
    private Long companyId;
}
