package com.chatq.assist.repository;

import com.chatq.assist.domain.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findByTenantId(String tenantId);

    Optional<Tenant> findByApiKey(String apiKey);

    List<Tenant> findByIsActiveOrderByCreatedAtDesc(Boolean isActive);

    List<Tenant> findAllByOrderByCreatedAtDesc();

    boolean existsByTenantId(String tenantId);

    boolean existsByApiKey(String apiKey);
}
