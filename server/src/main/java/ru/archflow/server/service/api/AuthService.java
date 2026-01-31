package ru.archflow.server.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.archflow.server.model.dto.user.AuthResponse;
import ru.archflow.server.model.dto.user.LoginRequest;
import ru.archflow.server.model.entity.list.User;
import ru.archflow.server.repository.UserRepository;
import ru.archflow.server.service.util.JwtUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

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