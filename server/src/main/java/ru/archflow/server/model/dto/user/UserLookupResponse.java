package ru.archflow.server.model.dto.user;

public record UserLookupResponse(Long id, String name, String email, String role) {
}
