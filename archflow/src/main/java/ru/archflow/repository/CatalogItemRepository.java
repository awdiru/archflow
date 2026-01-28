package ru.archflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.archflow.model.entity.enums.ItemCategory;
import ru.archflow.model.entity.list.CatalogItem;
import java.util.List;

public interface CatalogItemRepository extends JpaRepository<CatalogItem, Long> {
    List<CatalogItem> findAllByProjectId(Long projectId);
    List<CatalogItem> findAllByProjectIdAndCategory(Long projectId, ItemCategory category);
}