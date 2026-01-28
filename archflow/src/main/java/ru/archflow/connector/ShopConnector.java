package ru.archflow.connector;


import ru.archflow.model.dto.catalog.CatalogItemResponse;

public interface ShopConnector {
    boolean supports(String url);
    CatalogItemResponse parse(String url);
}
