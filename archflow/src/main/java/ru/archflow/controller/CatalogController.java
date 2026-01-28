package ru.archflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.archflow.model.dto.catalog.CatalogItemRequest;
import ru.archflow.model.entity.enums.ItemCategory;
import ru.archflow.model.entity.list.User;
import ru.archflow.service.api.CatalogService;
import ru.archflow.service.util.ScraperService;

@RestController
@RequestMapping("/api/projects/{projectId}/catalog")
@RequiredArgsConstructor
@Tag(name = "Catalog", description = "Управление товарами и сметой проекта")
public class CatalogController {

    private final CatalogService catalogService;
    private final ScraperService scraperService;

    @PostMapping
    @Operation(summary = "Добавить товар в смету проекта")
    public ResponseEntity<?> create(@PathVariable Long projectId,
                                    @RequestBody CatalogItemRequest request,
                                    @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(catalogService.createItem(projectId, request, currentUser.getId()));
    }

    @GetMapping
    @Operation(summary = "Получить товары проекта с фильтрацией по категории")
    public ResponseEntity<?> getAll(@PathVariable Long projectId,
                                    @RequestParam(required = false) ItemCategory category,
                                    @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(catalogService.getItems(projectId, category, currentUser.getId()));
    }

    @PutMapping("/{itemId}")
    @Operation(summary = "Обновить данные о товаре")
    public ResponseEntity<?> update(@PathVariable Long projectId,
                                    @PathVariable Long itemId,
                                    @RequestBody CatalogItemRequest request,
                                    @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(catalogService.updateItem(projectId, itemId, request, currentUser.getId()));
    }

    @DeleteMapping("/{itemId}")
    @Operation(summary = "Удалить товар из сметы")
    public ResponseEntity<?> delete(@PathVariable Long projectId,
                                    @PathVariable Long itemId,
                                    @AuthenticationPrincipal User currentUser) {

        catalogService.deleteItem(projectId, itemId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/auto-fill")
    @Operation(summary = "Получить данные о товаре по ссылке")
    public ResponseEntity<?> autoFill(@RequestParam String url) {

        return ResponseEntity.ok(scraperService.scrape(url));
    }
}