package ru.archflow.service.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MailService {
    private final JavaMailSender mailSender;

    @Value("${app.app-address}")
    private String address;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendConfirmationMail(String registerMail, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(registerMail);
        message.setSubject("Подтверждение регистрации на сайте " + address);
        message.setText("Подтвердите регистрацию, перейдя по ссылке:\n" + address + "/api/auth/confirm?token=" + token);
        mailSender.send(message);
    }
}
