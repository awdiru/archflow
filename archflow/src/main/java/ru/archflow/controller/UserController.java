package ru.archflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.archflow.service.api.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users Management", description = "Управление пользователями")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {
    private final UserService userService;


    @GetMapping("/search")
    @Operation(summary = "Поиск пользователей по совпадениям с именем или email")
    public ResponseEntity<?> searchUsers(@RequestParam String query) {
        return ResponseEntity.ok(userService.searchUsers(query));
    }
}
