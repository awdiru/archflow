package ru.archflow.server.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.archflow.server.model.entity.enums.Role;
import ru.archflow.server.model.entity.list.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    @Query("""
            SELECT u FROM User u WHERE
            (LOWER(u.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR
            LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')))
            AND u.isEnabled = true
            AND u.company.id = :companyId
            """)
    List<User> searchUsers(@Param("query") String query,
                           @Param("companyId") Long companyId, Pageable pageable);


    List<User> findByRole(Role role);
}
