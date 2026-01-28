package ru.archflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.archflow.model.entity.enums.ProjectStatus;
import ru.archflow.model.entity.list.Project;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("""
            SELECT DISTINCT p FROM Project p
            LEFT JOIN p.members m
            WHERE p.owner.id = :userId
            OR m.user.id = :userId
            """)
    List<Project> findAllByUserId(@Param("userId") Long userId);

    @Query("""
            SELECT DISTINCT p FROM Project p
            LEFT JOIN p.members m
            WHERE (p.owner.id = :userId
            OR m.user.id = :userId)
            AND p.status = :status
            """)
    List<Project> findAllByUserIdAndStatus(@Param("userId") Long userId, @Param("status") ProjectStatus status);
}