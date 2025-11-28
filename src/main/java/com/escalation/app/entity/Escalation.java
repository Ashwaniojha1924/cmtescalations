package com.escalation.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "escalations")
@Data
public class Escalation {

    private static final Logger logger = LoggerFactory.getLogger(Escalation.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "escalation_id")
    private Long escalationId;

    @Column(name = "patient_id")
    private String patientId;

    @Column(name = "created_by")
    private String createdBy;

    private String priority;
    private String status;

    @Column(name = "summary")
    private String summary;

    @Column(name = "notes")
    private String notes;

    @Column(name = "admission_id")
    private String admissionId;

    @Column(name = "facility_id")
    private String facilityId;

    @Column(name = "updated_by")
    private String updatedBy;

    // 🔹 Just a raw value from request
    @Column(name = "alert_id")
    private Long alertId;

    // 🔹 Specification is now part of Escalation
    @ManyToOne
    @JoinColumn(name = "specification_id")
    private Specification specification;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @OneToMany(mappedBy = "escalation", cascade = CascadeType.ALL, orphanRemoval = true)

    private List<EscalationNote> escalationNotes;

    @OneToMany(mappedBy = "escalation", cascade = CascadeType.ALL, orphanRemoval = true)

    private List<Document> documents;

    @OneToMany(mappedBy = "escalation", cascade = CascadeType.ALL, orphanRemoval = true)

    private List<Conversation> conversations;
    @ManyToOne
    @JoinColumn(name = "parent_escalation_id")
    private Escalation parentEscalation;

    @OneToMany(mappedBy = "escalation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EscalationSpecification> escalationSpecifications;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "impact")
    private String impact;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.lastUpdated = now;
        logger.trace("Pre-persist: Setting createdAt and lastUpdated for escalation");
    }

    @PreUpdate
    public void preUpdate() {
        this.lastUpdated = LocalDateTime.now();
        logger.trace("Pre-update: Updating lastUpdated for escalation id: {}", this.escalationId);
    }
}
