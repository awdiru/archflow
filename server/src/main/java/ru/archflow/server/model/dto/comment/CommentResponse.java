package ru.archflow.server.model.dto.comment;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CommentResponse {
    private Long id;
    private String text;
    private Long authorId;
    private String authorName;
    private Long markerId;
    private Boolean isResolved;
    private List<CommentResponse> replies; // Рекурсивный список
    private LocalDateTime createdAt;
}
