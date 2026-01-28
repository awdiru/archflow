package ru.archflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import ru.archflow.connector.petrovich.PetrovichApi;

@Configuration
public class RestClientConfig {

    @Bean
    public PetrovichApi petrovichApi(RestClient.Builder builder) {
        RestClient restClient = builder
                .baseUrl("https://api.petrovich.ru") // Указываем базовый домен API
                .defaultHeaders(headers -> {
                    headers.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
                    headers.add("Accept", "application/json, text/plain, */*");
                    headers.add("Referer", "https://petrovich.ru/");
                })
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();

        return factory.createClient(PetrovichApi.class);
    }
}