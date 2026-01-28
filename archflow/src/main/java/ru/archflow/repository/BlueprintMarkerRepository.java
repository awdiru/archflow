package ru.archflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.archflow.model.entity.list.BlueprintMarker;

import java.math.BigDecimal;
import java.util.List;

public interface BlueprintMarkerRepository extends JpaRepository<BlueprintMarker, Long> {
    @Query("""
            SELECT SUM(m.quantity * i.price)
            FROM BlueprintMarker m
            JOIN m.catalogItem i
            WHERE i.project.id = :projectId
            """)
    BigDecimal calculateTotalBudgetByProjectId(@Param("projectId") Long projectId);

    List<BlueprintMarker> findAllByBlueprintId(Long blueprintId);
}