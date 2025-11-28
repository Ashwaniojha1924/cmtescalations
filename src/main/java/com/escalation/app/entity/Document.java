package com.escalation.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
public class Document {

    private static final Logger logger = LoggerFactory.getLogger(Document.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Long documentId;

    @ManyToOne
    @JoinColumn(name = "escalation_id")
    @JsonIgnore
    private Escalation escalation;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @Column(name = "uploaded_by")
    private Integer uploadedBy;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "doc_id")
    private Integer docId;

    @PrePersist
    public void prePersist() {
        this.uploadedAt = LocalDateTime.now();
        logger.trace("Pre-persist: Setting uploadedAt for document");
    }
}
