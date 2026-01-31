package ru.archflow.server.model.dto.user;

public record AuthResponse(String token, String email, String role) {
}
