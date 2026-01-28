package ru.archflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.archflow.model.entity.enums.BlueprintType;
import ru.archflow.model.entity.list.User;
import ru.archflow.service.api.BlueprintService;
import ru.archflow.service.util.MinioService;

import java.io.InputStream;

@RestController
@RequestMapping("/api/projects/{projectId}/blueprints")
@RequiredArgsConstructor
@Tag(name = "Blueprints", description = "Управление чертежами и файлами проекта")
public class BlueprintController {

    private final BlueprintService blueprintService;
    private final MinioService minioService;

    @PostMapping(consumes = "multipart/form-data")
    @Operation(summary = "Загрузить новую версию чертежа")
    public ResponseEntity<?> upload(@PathVariable Long projectId,
                                    @RequestParam (required = false) String name,
                                    @RequestParam BlueprintType type,
                                    @RequestParam(required = false) String changeLog,
                                    @RequestParam("file") MultipartFile file,
                                    @AuthenticationPrincipal User currentUser) {

        blueprintService.uploadBlueprint(projectId, name, type, changeLog, file, currentUser.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> download(@RequestParam String fileUrl) {

        InputStream stream = minioService.downloadFile(fileUrl);
        String contentDisposition = org.springframework.http.ContentDisposition.builder("attachment")
                .filename(fileUrl, java.nio.charset.StandardCharsets.UTF_8)
                .build()
                .toString();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(stream));
    }

    @GetMapping
    @Operation(summary = "Получить актуальные версии всех чертежей проекта")
    public ResponseEntity<?> getLatestBlueprints(@PathVariable Long projectId,
                                                 @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(blueprintService.getProjectBlueprints(projectId, currentUser.getId()));
    }

    @GetMapping("/history")
    @Operation(summary = "Получить историю версий конкретного чертежа")
    public ResponseEntity<?> getHistory(@PathVariable Long projectId,
                                        @RequestParam String name,
                                        @RequestParam BlueprintType type,
                                        @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(blueprintService.getVersionHistory(projectId, name, type, currentUser.getId()));
    }

    @PatchMapping("/{blueprintId}/approve")
    @Operation(summary = "Одобрить версию чертежа (снимает одобрение с других версий этого файла)")
    public ResponseEntity<Void> approve(@PathVariable Long projectId,
                                        @PathVariable Long blueprintId,
                                        @AuthenticationPrincipal User currentUser) {

        blueprintService.approveBlueprint(projectId, blueprintId, currentUser.getId());
        return ResponseEntity.ok().build();
    }
}