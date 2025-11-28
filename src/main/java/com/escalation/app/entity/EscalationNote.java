package com.escalation.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Entity
@Table(name = "escalation_notes")
@Data
public class EscalationNote {

    private static final Logger logger = LoggerFactory.getLogger(EscalationNote.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "note_id")
    private Long noteId;

    @ManyToOne
    @JoinColumn(name = "escalation_id")
    @JsonIgnore
    private Escalation escalation;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "note_type")
    private String noteType;

    @Column(name = "note")
    private String note;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        logger.trace("Pre-persist: Setting createdAt for escalation note");
    }
}
