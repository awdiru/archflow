package ru.archflow.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.archflow.common.model.dto.events.AuthNotificationEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumerService {

    private final MailService mailService;

    @KafkaListener(topics = "auth-notifications", groupId = "notification-group")
    public void listenAuth(AuthNotificationEvent event) {
        log.info("Received auth notification for: {}", event.getEmail());
        mailService.sendConfirmationMail(event);
    }
}