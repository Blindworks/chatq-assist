package com.chatq.assist.domain.entity;

import com.chatq.assist.config.VectorType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.time.Instant;

@Entity
@Table(name = "document_chunks", indexes = {
    @Index(name = "idx_document_chunks_document", columnList = "document_id"),
    @Index(name = "idx_document_chunks_tenant", columnList = "tenant_id")
})
@Getter
@Setter
public class DocumentChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Column(name = "chunk_index", nullable = false)
    private Integer chunkIndex;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Type(VectorType.class)
    @Column(name = "embedding", columnDefinition = "vector(1536)")
    private float[] embedding;

    @Column(name = "token_count")
    private Integer tokenCount;

    @Column(name = "metadata", columnDefinition = "JSONB")
    private String metadata; // JSON string for page numbers, headers, etc.

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
