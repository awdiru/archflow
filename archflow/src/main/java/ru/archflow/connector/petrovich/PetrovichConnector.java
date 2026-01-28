package ru.archflow.connector.petrovich;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.archflow.connector.ShopConnector;
import ru.archflow.model.dto.catalog.CatalogItemResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class PetrovichConnector implements ShopConnector {

    private final PetrovichApi petrovichApi;
    private static final Pattern ID_PATTERN = Pattern.compile("product/(\\d+)");

    @Override
    public boolean supports(String url) {
        return url != null && url.contains("petrovich.ru");
    }

    @Override
    @SuppressWarnings("unchecked")
    public CatalogItemResponse parse(String url) {
        String productId = extractId(url);

        // 1. Получаем JSON
        Map<String, Object> response = petrovichApi.getProductMarketingData(productId);

        // 2. Навигация по структуре (data -> [0] -> products -> [0])
        // Судя по вашему файлу "петрович json товар.txt", данные лежат в массиве под ключом data
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) response.get("data");
        if (dataList == null || dataList.isEmpty()) {
            throw new RuntimeException("API вернуло пустой блок данных");
        }

        // Ищем целевой товар в массиве продуктов первого блока
        Map<String, Object> product = ((List<Map<String, Object>>) dataList.get(0).get("products"))
                .stream()
                .filter(p -> p.get("code").toString().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Товар с ID " + productId + " не найден в ответе"));

        Map<String, Object> price = (Map<String, Object>) product.get("price");

        return CatalogItemResponse.builder()
                .name((String) product.get("title"))
                .supplierUrl(url)
                .sku(product.get("code").toString())
                .unit((String) product.get("unitTitle"))
                .price(new BigDecimal(price.get("retail").toString()))
                .imageUrl(getImageUrl(product))
                .build();
    }

    private String getImageUrl(Map<String, Object> product) {
        List<String> images = (List<String>) product.get("images");
        if (images != null && !images.isEmpty()) {
            String url = images.get(0);
            return url.startsWith("//") ? "https:" + url : url;
        }
        return null;
    }

    private String extractId(String url) {
        Matcher matcher = ID_PATTERN.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Неверный формат ссылки Петровича: " + url);
    }
}