package ru.archflow.server.model.dto.company;

import lombok.Data;

@Data
public class CompanyCreateRequest {
    private String companyName;
    private String inn;
    private String adminEmail;
    private String adminName;
    private String adminPassword;
}
