package ru.archflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.archflow.model.entity.enums.BlueprintType;
import ru.archflow.model.entity.list.Blueprint;

import java.util.List;
import java.util.Optional;

public interface BlueprintRepository extends JpaRepository<Blueprint, Long> {
    // Найти последнюю версию конкретного чертежа в проекте
    Optional<Blueprint> findFirstByProjectIdAndNameAndTypeOrderByVersionDesc(Long projectId, String name, BlueprintType type);

    // Найти все версии конкретного чертежа
    List<Blueprint> findAllByProjectIdAndNameAndTypeOrderByVersionDesc(Long projectId, String name, BlueprintType type);

    @Query("""
            SELECT b FROM Blueprint b WHERE b.project.id = :projectId AND b.version = (
            SELECT MAX(b2.version) FROM Blueprint b2
            WHERE b2.project.id = :projectId AND b2.name = b.name AND b2.type = b.type)
            """)
    List<Blueprint> findAllLatestByProjectId(@Param("projectId") Long projectId);
}
