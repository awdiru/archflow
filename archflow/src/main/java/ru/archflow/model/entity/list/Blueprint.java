package ru.archflow.model.entity.list;

import jakarta.persistence.*;
import lombok.*;
import ru.archflow.model.entity.BaseEntity;
import ru.archflow.model.entity.enums.BlueprintType;

@Entity
@Table(name = "blueprints")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Blueprint extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String name;

    @Column(name = "change_log", columnDefinition = "TEXT")
    private String changeLog;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BlueprintType type;

    @Column(nullable = false)
    private String fileUrl;

    @Builder.Default
    private Integer version = 1;

    @Builder.Default
    private Boolean isApproved = false;
}