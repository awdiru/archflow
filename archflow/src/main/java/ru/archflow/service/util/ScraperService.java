package ru.archflow.service.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.archflow.connector.ShopConnector;
import ru.archflow.model.dto.catalog.CatalogItemResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScraperService {

    private final List<ShopConnector> connectors;

    public CatalogItemResponse scrape(String url) {
        return connectors.stream()
                .filter(connector -> connector.supports(url))
                .findFirst()
                .map(connector -> connector.parse(url))
                .orElseThrow(() -> new IllegalArgumentException("Данный магазин пока не поддерживается"));
    }
}
