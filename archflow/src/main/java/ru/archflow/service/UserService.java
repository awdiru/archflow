package ru.archflow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.archflow.model.dto.user.UserLookupResponse;
import ru.archflow.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserLookupResponse> searchUsers(String query) {
        return userRepository.searchUsers(query, PageRequest.of(0, 10))
                .stream()
                .map(user -> new UserLookupResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole().name()))
                .toList();
    }
}
