package ru.archflow.connector.petrovich;

import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;

@HttpExchange("/catalog/v5/marketing/products")
public interface PetrovichApi {

    @GetExchange("/{id}?pet_case=camel&city_code=spb&client_id=pet_site")
    Map<String, Object> getProductMarketingData(@PathVariable("id") String id);
}