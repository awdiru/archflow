package ru.archflow.server.model.dto.catalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.archflow.server.model.entity.enums.ItemCategory;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogItemRequest {
    private String name;
    private ItemCategory category;
    private String supplierUrl;
    private String imageUrl;
    private BigDecimal price;
    private String unit;
    private String sku;
}