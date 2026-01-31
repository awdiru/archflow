package ru.archflow.server.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public NewTopic authTopic() {
        return TopicBuilder.name("auth-notifications")
                .partitions(1)
                .replicas(1) // В KRaft режиме для одного узла ставим 1
                .build();
    }
}