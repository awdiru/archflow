package ru.archflow.server.model.entity.list;

import jakarta.persistence.*;
import lombok.*;
import ru.archflow.server.model.entity.BaseEntity;
import ru.archflow.server.model.entity.enums.ItemCategory;

import java.math.BigDecimal;

@Entity
@Table(name = "catalog_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatalogItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemCategory category;

    private String supplierUrl;

    private String imageUrl;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private String unit;

    private String sku;
}