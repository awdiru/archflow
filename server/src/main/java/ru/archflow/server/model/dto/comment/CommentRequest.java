package ru.archflow.server.model.dto.comment;

import lombok.Data;

@Data
public class CommentRequest {
    private String text;
    private Long markerId;
    private Long parentId;
}
