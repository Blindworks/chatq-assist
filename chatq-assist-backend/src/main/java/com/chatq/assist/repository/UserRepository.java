package com.chatq.assist.repository;

import com.chatq.assist.domain.entity.User;
import com.chatq.assist.domain.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndTenantId(String username, String tenantId);

    List<User> findByTenantId(String tenantId);

    List<User> findByTenantIdAndRole(String tenantId, UserRole role);

    List<User> findByTenant_Id(Long tenantRefId);

    boolean existsByUsername(String username);

    long countByTenantId(String tenantId);
}
