package ru.archflow.server.service.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.archflow.common.model.dto.events.AuthNotificationEvent;
import ru.archflow.server.model.entity.enums.ProjectRole;
import ru.archflow.server.model.entity.enums.Role;
import ru.archflow.server.model.entity.list.ProjectMember;
import ru.archflow.server.model.entity.list.User;
import ru.archflow.server.repository.ProjectMemberRepository;
import ru.archflow.server.repository.UserRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserUtilService {
    private final ProjectMemberRepository memberRepository;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void validateProjectRoleAccess(Long projectId, Long userId, ProjectRole... roles) {
        ProjectMember user = memberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new RuntimeException("Access denied"));

        for (ProjectRole role : roles) {
            if (user.getRole().equals(role))
                return;
        }
        throw new RuntimeException("Access denied");
    }

    public User validateRoleAccess(Long userId, Role... role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        for (Role r : role) {
            if (user.getRole().equals(r))
                return user;
        }
        throw new RuntimeException("Access denied");
    }

    public void saveUserAndNotify(User user, String rawPassword) {
        userRepository.save(user);
        String confirmationToken = jwtUtils.generateToken(user.getEmail());
        String companyName = (user.getCompany() != null) ? user.getCompany().getCompanyName() : "ArchFlow";

        AuthNotificationEvent event = AuthNotificationEvent.builder()
                .company(companyName)
                .email(user.getEmail())
                .rawPassword(rawPassword)
                .token(confirmationToken)
                .build();

        kafkaTemplate.send("auth-notifications", event);
        log.info("Sent registration event to Kafka for user: {}", user.getEmail());
    }

    public String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
