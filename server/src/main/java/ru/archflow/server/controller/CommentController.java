package ru.archflow.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.archflow.server.model.dto.comment.CommentRequest;
import ru.archflow.server.model.entity.list.User;
import ru.archflow.server.service.api.CommentService;

@RestController
@RequestMapping("/api/projects/{projectId}/blueprints/{blueprintId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<?> add(@PathVariable Long projectId,
                                 @PathVariable Long blueprintId,
                                 @RequestBody CommentRequest request,
                                 @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(commentService.addComment(projectId, blueprintId, request, currentUser));
    }

    @GetMapping
    public ResponseEntity<?> getComments(@PathVariable Long projectId,
                                         @PathVariable Long blueprintId,
                                         @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(commentService.getBlueprintComments(projectId, blueprintId, currentUser.getId()));
    }

    @PatchMapping("/{commentId}/resolve")
    public ResponseEntity<Void> resolve(@PathVariable Long projectId,
                                        @PathVariable Long commentId,
                                        @AuthenticationPrincipal User currentUser) {

        commentService.resolveComment(projectId, commentId, currentUser.getId());
        return ResponseEntity.ok().build();
    }
}
