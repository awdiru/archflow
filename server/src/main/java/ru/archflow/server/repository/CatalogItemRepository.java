package ru.archflow.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.archflow.server.model.entity.enums.ItemCategory;
import ru.archflow.server.model.entity.list.CatalogItem;
import java.util.List;

public interface CatalogItemRepository extends JpaRepository<CatalogItem, Long> {
    List<CatalogItem> findAllByProjectId(Long projectId);
    List<CatalogItem> findAllByProjectIdAndCategory(Long projectId, ItemCategory category);
}