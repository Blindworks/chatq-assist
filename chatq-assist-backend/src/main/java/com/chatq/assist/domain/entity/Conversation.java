package com.chatq.assist.domain.entity;

import com.chatq.assist.domain.enums.ConversationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conversations", indexes = {
    @Index(name = "idx_conversation_tenant_session", columnList = "tenant_id, session_id"),
    @Index(name = "idx_conversation_tenant_status", columnList = "tenant_id, status")
})
@Getter
@Setter
public class Conversation extends BaseEntity {

    @Column(name = "session_id", nullable = false, unique = true)
    private String sessionId;

    @Column(name = "user_email")
    private String userEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ConversationStatus status = ConversationStatus.ACTIVE;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    @Column(name = "last_activity_at")
    private java.time.Instant lastActivityAt;
}
