package ru.archflow.server.model.entity.list;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import ru.archflow.server.model.entity.BaseEntity;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company extends BaseEntity {
    @Column(name = "company_name", nullable = false, unique = true)
    private String companyName;

    private String inn;

    @Column(name = "is_active")
    private boolean isActive = true;
}
