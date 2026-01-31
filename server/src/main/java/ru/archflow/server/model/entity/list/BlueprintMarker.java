package ru.archflow.server.model.entity.list;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.archflow.server.model.entity.BaseEntity;

import java.math.BigDecimal;
import java.util.Map;

@Entity
@Table(name = "blueprint_markers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlueprintMarker extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blueprint_id", nullable = false)
    private Blueprint blueprint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catalog_item_id", nullable = false)
    private CatalogItem catalogItem;

    // Рассчитанное количество (например, площадь полигона * коэф запаса)
    @Column(nullable = false)
    private BigDecimal quantity;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "coordinates", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> coordinates;
}