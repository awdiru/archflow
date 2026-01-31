package ru.archflow.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.archflow.server.model.dto.company.CompanyCreateRequest;
import ru.archflow.server.model.dto.user.UserCreateRequest;
import ru.archflow.server.model.entity.list.User;
import ru.archflow.server.service.api.AuthService;
import ru.archflow.server.service.api.CompanyService; // Предположим, логику лучше держать тут

@RestController
@RequestMapping("/api/management")
@RequiredArgsConstructor
@Tag(name = "Company Management", description = "Управление компаниями и персоналом")
@SecurityRequirement(name = "Bearer Authentication")
public class CompanyController {

    private final AuthService authService;
    private final CompanyService companyService;

    @PostMapping("/companies")
    @Operation(summary = "Регистрация новой компании (Только для Super Admin)")
    public ResponseEntity<?> registerCompany(@RequestBody CompanyCreateRequest request,
                                             @AuthenticationPrincipal User currentUser) {

        companyService.createCompanyWithAdmin(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Компания и администратор успешно созданы");
    }

    @PostMapping("/users")
    @Operation(summary = "Регистрация сотрудника внутри компании (Admin или Designer)")
    public ResponseEntity<?> createEmployee(@RequestBody UserCreateRequest request,
                                            @AuthenticationPrincipal User currentUser) {

        companyService.createUserInCompany(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body("Сотрудник успешно зарегистрирован");
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск пользователей по совпадениям с именем или email")
    public ResponseEntity<?> searchUsers(@RequestParam String query,
                                         @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(companyService.searchUsers(currentUser.getId(), query));
    }
}