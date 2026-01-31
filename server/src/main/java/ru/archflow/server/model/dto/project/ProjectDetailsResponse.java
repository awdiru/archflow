package ru.archflow.server.model.dto.project;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class ProjectDetailsResponse extends ProjectResponse {
    private List<ProjectMemberResponse> members;
}
