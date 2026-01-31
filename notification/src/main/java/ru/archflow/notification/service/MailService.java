package ru.archflow.notification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ru.archflow.common.model.dto.events.AuthNotificationEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine; // Движок шаблонов

    @Value("${spring.application.app-address}")
    private String address;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendConfirmationMail(AuthNotificationEvent event) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            Context context = new Context();
            String url = address + "/api/auth/confirm?token=" + event.getToken();
            context.setVariable("password", event.getRawPassword());
            context.setVariable("company", event.getCompany());
            context.setVariable("confirmationUrl", url);

            String htmlContent = templateEngine.process("auth-confirmation", context);

            helper.setFrom(fromEmail);
            helper.setTo(event.getEmail());
            helper.setSubject(event.getCompany() + " | Подтверждение регистрации");
            helper.setText(htmlContent, true); // true указывает, что это HTML

            mailSender.send(mimeMessage);
            log.info("HTML confirmation mail sent to {}", event.getEmail());

        } catch (MessagingException e) {
            log.error("Failed to send HTML email to {}", event.getEmail(), e);
            throw new RuntimeException("Error sending email", e);
        }
    }
}