package com.escalation.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_trail")
@Data
@Setter
@Getter
public class AuditTrail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Long auditId;

    @Column(name = "escalation_id")
    private Long escalationId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "actor_type")
    private String actorType;

    @Column(name = "actor_id")
    private Integer actorId;

    private String action;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;
}
