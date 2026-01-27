package ru.archflow.model.dto.user;

public record AuthResponse(String token, String email, String role) {
}
