package ru.archflow.server.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.archflow.server.model.dto.company.CompanyCreateRequest;
import ru.archflow.server.model.dto.user.UserCreateRequest;
import ru.archflow.server.model.dto.user.UserLookupResponse;
import ru.archflow.server.model.entity.enums.Role;
import ru.archflow.server.model.entity.list.Company;
import ru.archflow.server.model.entity.list.User;
import ru.archflow.server.repository.CompanyRepository;
import ru.archflow.server.repository.UserRepository;
import ru.archflow.server.service.util.UserUtilService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserUtilService userUtilService;

    @Transactional
    public void createCompanyWithAdmin(Long userId, CompanyCreateRequest request) {
        userUtilService.validateRoleAccess(userId, Role.SUPER_ADMIN);

        if (companyRepository.existsByCompanyName(request.getCompanyName()))
            throw new RuntimeException("Company with this name already exists");

        Company company = Company.builder()
                .companyName(request.getCompanyName())
                .inn(request.getInn())
                .isActive(true)
                .build();
        company = companyRepository.save(company);

        String tempPassword = userUtilService.generateRandomPassword();
        User admin = User.builder()
                .email(request.getAdminEmail())
                .fullName(request.getAdminName())
                .role(Role.COMPANY_ADMIN)
                .company(company)
                .passwordHash(passwordEncoder.encode(tempPassword))
                .isEnabled(false)
                .build();

        userUtilService.saveUserAndNotify(admin, tempPassword);
    }

    @Transactional
    public void createUserInCompany(UserCreateRequest request, Long userId) {
        User user = userUtilService.validateRoleAccess(userId, Role.SUPER_ADMIN, Role.COMPANY_ADMIN);

        Company targetCompany;
        if (user.getRole() == Role.COMPANY_ADMIN) {
            if (request.getRole() != Role.CLIENT
                    && request.getRole() != Role.DESIGNER
                    && request.getRole() != Role.CONTRACTOR) {

                throw new RuntimeException("Invalid role");
            }
            targetCompany = user.getCompany();
        } else {
            targetCompany = companyRepository.findById(request.getCompanyId())
                    .orElse(null);
        }

        String tempPassword = userUtilService.generateRandomPassword();
        User newUser = User.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .role(request.getRole())
                .company(targetCompany)
                .passwordHash(passwordEncoder.encode(tempPassword))
                .isEnabled(false)
                .build();

        userUtilService.saveUserAndNotify(newUser, tempPassword);
    }

    public List<UserLookupResponse> searchUsers(Long userId, String query) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userRepository.searchUsers(query, user.getCompany().getId(), PageRequest.of(0, 10))
                .stream()
                .map(u -> new UserLookupResponse(u.getId(), u.getFullName(), u.getEmail(), u.getRole().name()))
                .toList();
    }
}
