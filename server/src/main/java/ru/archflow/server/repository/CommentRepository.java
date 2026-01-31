package ru.archflow.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.archflow.server.model.entity.list.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByBlueprintIdAndParentIsNullOrderByCreatedAtDesc(Long blueprintId);
}
