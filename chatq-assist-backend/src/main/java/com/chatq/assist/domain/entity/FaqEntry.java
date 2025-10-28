package com.chatq.assist.domain.entity;

import com.chatq.assist.config.VectorType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "faq_entries", indexes = {
    @Index(name = "idx_tenant_active", columnList = "tenant_id, is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FaqEntry extends BaseEntity {

    @Column(name = "question", nullable = false, length = 1000)
    private String question;

    @Column(name = "answer", nullable = false, columnDefinition = "TEXT")
    private String answer;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "faq_tags", joinColumns = @JoinColumn(name = "faq_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "usage_count")
    private Long usageCount = 0L;

    @Type(VectorType.class)
    @Column(name = "embedding", columnDefinition = "vector(1536)")
    private float[] embedding;
}
