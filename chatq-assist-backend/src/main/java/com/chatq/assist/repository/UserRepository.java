package com.chatq.assist.repository;

import com.chatq.assist.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndTenantId(String username, String tenantId);

    boolean existsByUsername(String username);
}
