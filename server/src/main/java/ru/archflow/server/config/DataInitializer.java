package ru.archflow.server.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.archflow.server.model.entity.enums.Role;
import ru.archflow.server.model.entity.list.User;
import ru.archflow.server.repository.UserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.application.admin-email}")
    private String adminEmail;
    @Value("${spring.application.admin-password}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByRole(Role.SUPER_ADMIN).isEmpty()) {
            User admin = User.builder()
                    .email(adminEmail)
                    .passwordHash(passwordEncoder.encode(adminPassword))
                    .fullName("Platform Administrator")
                    .role(Role.SUPER_ADMIN)
                    .isEnabled(true)
                    .build();
            userRepository.save(admin);
            log.info("Super Admin account created: admin@archflow.ru");
        }
    }
}