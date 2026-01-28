package ru.archflow.service.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.archflow.model.dto.catalog.CatalogItemRequest;
import ru.archflow.model.dto.catalog.CatalogItemResponse;
import ru.archflow.model.entity.enums.ItemCategory;
import ru.archflow.model.entity.enums.ProjectRole;
import ru.archflow.model.entity.list.CatalogItem;
import ru.archflow.repository.CatalogItemRepository;
import ru.archflow.repository.ProjectRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final CatalogItemRepository catalogRepository;
    private final ProjectRepository projectRepository;
    private final ProjectService projectService;

    @Transactional
    public CatalogItemResponse createItem(Long projectId, CatalogItemRequest request, Long userId) {
        projectService.validateRoleAccess(projectId, userId, ProjectRole.OWNER, ProjectRole.EDITOR);

        CatalogItem item = CatalogItem.builder()
                .project(projectRepository.getReferenceById(projectId))
                .name(request.getName())
                .category(request.getCategory())
                .supplierUrl(request.getSupplierUrl())
                .imageUrl(request.getImageUrl())
                .price(request.getPrice())
                .unit(request.getUnit())
                .sku(request.getSku())
                .build();

        return mapToResponse(catalogRepository.save(item));
    }

    @Transactional(readOnly = true)
    public List<CatalogItemResponse> getItems(Long projectId, ItemCategory category, Long userId) {
        projectService.validateRoleAccess(projectId, userId, ProjectRole.values());

        List<CatalogItem> items;
        if (category != null) {
            items = catalogRepository.findAllByProjectIdAndCategory(projectId, category);
        } else {
            items = catalogRepository.findAllByProjectId(projectId);
        }

        return items.stream().map(this::mapToResponse).toList();
    }

    @Transactional
    public CatalogItemResponse updateItem(Long projectId, Long itemId, CatalogItemRequest request, Long userId) {
        projectService.validateRoleAccess(projectId, userId, ProjectRole.OWNER, ProjectRole.EDITOR);

        CatalogItem item = catalogRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.getProject().getId().equals(projectId)) {
            throw new RuntimeException("Item does not belong to this project");
        }

        item.setName(request.getName());
        item.setCategory(request.getCategory());
        item.setPrice(request.getPrice());
        item.setUnit(request.getUnit());
        item.setSku(request.getSku());
        item.setSupplierUrl(request.getSupplierUrl());
        item.setImageUrl(request.getImageUrl());

        return mapToResponse(catalogRepository.save(item));
    }

    @Transactional
    public void deleteItem(Long projectId, Long itemId, Long userId) {
        projectService.validateRoleAccess(projectId, userId, ProjectRole.OWNER, ProjectRole.EDITOR);

        CatalogItem item = catalogRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.getProject().getId().equals(projectId)) {
            throw new RuntimeException("Access denied");
        }

        catalogRepository.delete(item);
    }

    private CatalogItemResponse mapToResponse(CatalogItem item) {
        return CatalogItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .category(item.getCategory())
                .price(item.getPrice())
                .unit(item.getUnit())
                .sku(item.getSku())
                .supplierUrl(item.getSupplierUrl())
                .imageUrl(item.getImageUrl())
                .createdAt(item.getCreatedAt())
                .build();
    }
}