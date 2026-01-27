package ru.archflow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.archflow.model.dto.user.AuthResponse;
import ru.archflow.model.dto.user.LoginRequest;
import ru.archflow.model.dto.user.RegisterRequest;
import ru.archflow.model.entity.list.User;
import ru.archflow.model.entity.enums.Role;
import ru.archflow.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public void register(RegisterRequest request) {
        // Проверка на существование
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .role(Role.valueOf(request.role().toUpperCase()))
                .build();

        userRepository.save(user);

        // Генерируем временный токен для подтверждения (можно использовать тот же JWT)
        String confirmationToken = jwtUtils.generateToken(user.getEmail());
        mailService.sendConfirmationMail(user.getEmail(), confirmationToken);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtUtils.generateToken(user.getEmail());

        return new AuthResponse(token, user.getEmail(), user.getRole().name());
    }

    @Transactional
    public void confirmEmail(String token) {
        if (!jwtUtils.validateJwtToken(token)) {
            throw new RuntimeException("Invalid or expired confirmation token");
        }

        String email = jwtUtils.getUserNameFromJwtToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isEnabled()) {
            throw new RuntimeException("Account is already activated");
        }

        user.setEnabled(true);
        userRepository.save(user);
    }
}