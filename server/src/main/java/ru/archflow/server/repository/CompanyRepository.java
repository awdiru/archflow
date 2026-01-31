package ru.archflow.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.archflow.server.model.entity.list.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsByCompanyName(String name);
}
