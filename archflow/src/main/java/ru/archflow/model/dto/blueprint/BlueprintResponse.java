package ru.archflow.model.dto.blueprint;

import lombok.Builder;
import lombok.Data;
import ru.archflow.model.entity.enums.BlueprintType;
import java.time.LocalDateTime;

@Data
@Builder
public class BlueprintResponse {
    private Long id;
    private String name;
    private BlueprintType type;
    private String fileUrl;
    private Integer version;
    private Boolean isApproved;
    private String changeLog;
    private LocalDateTime createdAt;
}