package ru.archflow.model.dto.marker;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class CreateMarkerRequest {
    private Long catalogItemId;
    private BigDecimal quantity;
    private Map<String, Object> coordinates;
}
