package ru.archflow.model.dto.project;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import ru.archflow.model.entity.enums.ProjectStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@SuperBuilder
public class ProjectResponse {
    private Long id;
    private String name;
    private String description;
    private String address;
    private ProjectStatus status;
    private BigDecimal totalBudget;
    private Long ownerId;
    private String ownerFullName;
    private LocalDateTime createdAt;
}