package ru.archflow.server.service.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.archflow.server.model.dto.comment.CommentRequest;
import ru.archflow.server.model.dto.comment.CommentResponse;
import ru.archflow.server.model.entity.enums.ProjectRole;
import ru.archflow.server.model.entity.list.Blueprint;
import ru.archflow.server.model.entity.list.Comment;
import ru.archflow.server.model.entity.list.User;
import ru.archflow.server.repository.BlueprintMarkerRepository;
import ru.archflow.server.repository.BlueprintRepository;
import ru.archflow.server.repository.CommentRepository;
import ru.archflow.server.service.util.UserUtilService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final ProjectService projectService;
    private final BlueprintRepository blueprintRepository;
    private final BlueprintMarkerRepository markerRepository;
    private final UserUtilService userUtilService;

    @Transactional
    public CommentResponse addComment(Long projectId, Long blueprintId, CommentRequest request, User author) {
        userUtilService.validateProjectRoleAccess(projectId, author.getId(), ProjectRole.values());
        Blueprint blueprint = blueprintRepository.getReferenceById(blueprintId);

        Comment comment = Comment.builder()
                .text(request.getText())
                .author(author)
                .blueprint(blueprint)
                .isResolved(false)
                .build();

        if (request.getMarkerId() != null) {
            comment.setMarker(markerRepository.getReferenceById(request.getMarkerId()));
        }

        if (request.getParentId() != null) {
            comment.setParent(commentRepository.getReferenceById(request.getParentId()));
        }

        return mapToResponse(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getBlueprintComments(Long projectId, Long blueprintId, Long userId) {
        userUtilService.validateProjectRoleAccess(projectId, userId, ProjectRole.values());

        return commentRepository.findAllByBlueprintIdAndParentIsNullOrderByCreatedAtDesc(blueprintId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public void resolveComment(Long projectId, Long commentId, Long userId) {
        userUtilService.validateProjectRoleAccess(projectId, userId, ProjectRole.OWNER, ProjectRole.EDITOR);
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        comment.setIsResolved(true);
        commentRepository.save(comment);
    }

    private CommentResponse mapToResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorId(comment.getAuthor().getId())
                .authorName(comment.getAuthor().getFullName())
                .markerId(comment.getMarker() != null ? comment.getMarker().getId() : null)
                .isResolved(comment.getIsResolved())
                .createdAt(comment.getCreatedAt())
                .replies(comment.getReplies().stream().map(this::mapToResponse).toList())
                .build();
    }
}
