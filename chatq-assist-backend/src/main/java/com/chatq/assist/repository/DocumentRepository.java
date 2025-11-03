package com.chatq.assist.repository;

import com.chatq.assist.domain.entity.Document;
import com.chatq.assist.domain.enums.DocumentStatus;
import com.chatq.assist.domain.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByTenantIdOrderByCreatedAtDesc(String tenantId);

    List<Document> findByTenantIdAndStatus(String tenantId, DocumentStatus status);

    List<Document> findByTenantIdAndDocumentType(String tenantId, DocumentType documentType);

    List<Document> findByTenantIdAndStatusOrderByCreatedAtDesc(String tenantId, DocumentStatus status);

    long countByTenantIdAndStatus(String tenantId, DocumentStatus status);

    long countByTenantId(String tenantId);
}
