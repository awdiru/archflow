package ru.archflow.model.dto.user;

public record RegisterRequest(String email, String password, String fullName, String role) {
}
