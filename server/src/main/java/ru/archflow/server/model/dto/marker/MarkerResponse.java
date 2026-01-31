package ru.archflow.server.model.dto.marker;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class MarkerResponse {
    private Long id;
    private Long blueprintId;
    private Long catalogItemId;
    private String catalogItemName;
    private BigDecimal quantity;
    private Map<String, Object> coordinates;
}
