package ru.archflow.common.model.dto.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthNotificationEvent {
    private String company;
    private String email;
    private String rawPassword;
    private String token;
}