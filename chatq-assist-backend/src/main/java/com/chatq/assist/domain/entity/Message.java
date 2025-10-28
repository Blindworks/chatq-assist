package com.chatq.assist.domain.entity;

import com.chatq.assist.domain.enums.MessageRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "messages", indexes = {
    @Index(name = "idx_message_conversation", columnList = "conversation_id"),
    @Index(name = "idx_message_created", columnList = "created_at")
})
@Getter
@Setter
public class Message extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private MessageRole role;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "faq_entry_id")
    private Long faqEntryId;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON string for additional data
}
