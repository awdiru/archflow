package ru.archflow.server.connector;


import ru.archflow.server.model.dto.catalog.CatalogItemResponse;

public interface ShopConnector {
    boolean supports(String url);
    CatalogItemResponse parse(String url);
}
