package com.escalation.app.service;

import com.escalation.app.entity.*;
import com.escalation.app.repository.*;
import com.escalation.app.request.CloseEscalationRequest;
import com.escalation.app.request.EscalationCreateRequest;
import com.escalation.app.request.ReEscalateRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EscalationService {

    private static final Logger logger = LoggerFactory.getLogger(EscalationService.class);

    private final EscalationRepository escalationRepository;
    private final EscalationNoteRepository escalationNoteRepository;
    private final DocumentRepository documentRepository;
    private final SpecificationRepository specificationRepository;
    private final AuditService auditService;
    private final EscalationSpecificationRepository escalationSpecificationRepository;

    public Escalation createEscalationWithChildren(EscalationCreateRequest req) {
        logger.debug("Creating escalation with children for patientId: {}, admissionId: {}", req.getPatientId(), req.getAdmissionId());

        Escalation escalation = new Escalation();
        escalation.setPatientId(req.getPatientId());
        escalation.setCreatedBy(req.getCreatedBy());
        escalation.setPriority(req.getPriority());
        escalation.setStatus(req.getStatus());
      //  escalation.setSummary(req.getSummary());
       // escalation.setNotes(req.getNotes());
        escalation.setAdmissionId(req.getAdmissionId());
        escalation.setFacilityId(req.getFacilityId());
        escalation.setUpdatedBy(req.getUpdatedBy());
        escalation.setAlertId(req.getAlertId());

        // 🔹 Attach specification if provided
        if (req.getSpecificationId() != null) {
            logger.debug("Attaching specification id: {} to escalation", req.getSpecificationId());
            Specification spec = specificationRepository.findById(req.getSpecificationId())
                    .orElseThrow(() -> {
                        logger.error("Specification not found with id: {}", req.getSpecificationId());
                        return new RuntimeException("Specification not found");
                    });
            escalation.setSpecification(spec);
        }

        Escalation savedEscalation = escalationRepository.save(escalation);
        logger.debug("Saved escalation with id: {}", savedEscalation.getEscalationId());

        // Notes
        if (req.getEscalationNotes() != null) {
            logger.debug("Adding {} notes to escalation", req.getEscalationNotes().size());
            for (EscalationCreateRequest.NoteDto ndto : req.getEscalationNotes()) {
                EscalationNote note = new EscalationNote();
                note.setEscalation(savedEscalation);
                note.setCreatedBy(ndto.getCreatedBy());
                note.setNoteType(ndto.getNoteType());
                note.setNote(ndto.getNote());
                escalationNoteRepository.save(note);
            }
        }

        // Documents
        if (req.getDocuments() != null) {
            logger.debug("Adding {} documents to escalation", req.getDocuments().size());
            for (EscalationCreateRequest.DocumentDto ddto : req.getDocuments()) {
                Document doc = new Document();
                doc.setEscalation(savedEscalation);
                doc.setUploadedBy(ddto.getUploadedBy());
                doc.setFileName(ddto.getFileName());
                doc.setFileType(ddto.getFileType());
                doc.setDocId(ddto.getDocId());
                documentRepository.save(doc);
            }
        }
        if (req.getSpecifications() != null) {
            logger.debug("Adding {} specifications to escalation", req.getSpecifications().size());
            for (EscalationCreateRequest.SpecNoteDto dto : req.getSpecifications()) {
                Specification spec = specificationRepository.findById(dto.getSpecificationId())
                        .orElseThrow(() -> {
                            logger.error("Specification not found with id: {}", dto.getSpecificationId());
                            return new RuntimeException("Specification not found");
                        });

                EscalationSpecification es = new EscalationSpecification();
                es.setEscalation(savedEscalation);
                es.setSpecification(spec);
                es.setNotes(dto.getNotes());

                escalationSpecificationRepository.save(es);
            }
        }


        auditService.log(
                savedEscalation.getEscalationId(),
                "USER",
                null,
                "Escalation Created (with specification)",
                req
        );

        logger.info("Successfully created escalation with id: {} for patientId: {}", savedEscalation.getEscalationId(), req.getPatientId());
        return savedEscalation;
    }

    public Escalation getEscalation(Long id) {
        logger.debug("Fetching escalation with id: {}", id);
        return escalationRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Escalation not found with id: {}", id);
                    return new RuntimeException("Escalation not found");
                });
    }

    public List<AuditTrail> getAuditForEscalation(Long escalationId, AuditTrailRepository auditTrailRepository) {
        logger.debug("Fetching audit trail for escalation id: {}", escalationId);
        List<AuditTrail> auditTrails = auditTrailRepository.findAll().stream()
                .filter(a -> a.getEscalationId().equals(escalationId))
                .toList();
        logger.debug("Found {} audit trail entries for escalation id: {}", auditTrails.size(), escalationId);
        return auditTrails;
    }

    @Transactional
    public Escalation reEscalate(Long originalId, ReEscalateRequest req) {
        logger.info("Re-escalating escalation id: {} with new priority: {}", originalId, req.getPriority());

        Escalation original = escalationRepository.findById(originalId)
                .orElseThrow(() -> {
                    logger.error("Original escalation not found with id: {}", originalId);
                    return new RuntimeException("Original escalation not found");
                });

        // 1. Create new escalation based on original
        Escalation neo = new Escalation();
        neo.setPatientId(original.getPatientId());
        neo.setAdmissionId(original.getAdmissionId());
        neo.setFacilityId(original.getFacilityId());
        neo.setSummary(original.getSummary());
        neo.setNotes(req.getReEscalationNotes()); // new clinical story
        neo.setCreatedBy(req.getCreatedBy());
        neo.setUpdatedBy(req.getCreatedBy());
        neo.setPriority(req.getPriority() != null ? req.getPriority() : original.getPriority());
        neo.setStatus("OPEN");
        neo.setAlertId(req.getAlertId() != null ? req.getAlertId() : original.getAlertId());
        neo.setSpecification(
                req.getSpecificationId() != null
                        ? specificationRepository.findById(req.getSpecificationId())
                        .orElseThrow(() -> new RuntimeException("Specification not found"))
                        : original.getSpecification()
        );
        neo.setParentEscalation(original);   // self-link for “Past Escalations” tab

        Escalation savedNew = escalationRepository.save(neo);
        logger.debug("Created new escalation with id: {} from original id: {}", savedNew.getEscalationId(), originalId);

        // 2. Add RE_ESCALATION_NOTE
        EscalationNote note = new EscalationNote();
        note.setEscalation(savedNew);
        note.setCreatedBy(req.getCreatedBy()); // or map from user id
        note.setNoteType("RE_ESCALATION_NOTE");
        note.setNote(req.getReEscalationNotes());
        escalationNoteRepository.save(note);
        logger.debug("Added RE_ESCALATION_NOTE to new escalation");

        // 3. Attach documents (if you received IDs)

        if (req.getDocuments() != null) {
            logger.debug("Attaching {} documents to re-escalation", req.getDocuments().size());
            for (ReEscalateRequest.DocumentDto ddto : req.getDocuments()) {
                Document doc = new Document();
                doc.setEscalation(savedNew);
                doc.setUploadedBy(ddto.getUploadedBy());
                doc.setFileName(ddto.getFileName());
                doc.setFileType(ddto.getFileType());
                doc.setDocId(ddto.getDocId());
                documentRepository.save(doc);
            }
        }

        // 4. Mark original as RE_ESCALATED (or CLOSED_INVALID)
        original.setStatus("RE_ESCALATED");
        escalationRepository.save(original);
        logger.debug("Marked original escalation id: {} as RE_ESCALATED", originalId);

        // 5. Audit logs
        auditService.log(originalId, "USER", null,
                "Re-Escalated", Map.of("newEscalationId", savedNew.getEscalationId()));
        auditService.log(savedNew.getEscalationId(), "USER", null,
                "Escalation Created (Re-escalation)", req);

        logger.info("Successfully re-escalated. Original id: {}, New escalation id: {}", originalId, savedNew.getEscalationId());
        return savedNew;
    }

    @Transactional
    public Escalation closeEscalation(Long escalationId, CloseEscalationRequest req) {
        logger.info("Closing escalation id: {} by user: {}", escalationId, req.getClosedBy());

        Escalation escalation = escalationRepository.findById(escalationId)
                .orElseThrow(() -> {
                    logger.error("Escalation not found with id: {}", escalationId);
                    return new RuntimeException("Escalation not found");
                });

        // 1) Update escalation status + impact
        escalation.setStatus("CLOSED");               // or "COMPLETED"
        escalation.setUpdatedBy(req.getClosedBy());
        escalation.setImpact(req.getImpact());        // 🔹 may be null, that’s fine

        // (optional) if you added closedAt:
        // escalation.setClosedAt(LocalDateTime.now());

        Escalation saved = escalationRepository.save(escalation);
        logger.debug("Updated escalation id: {} status to CLOSED", escalationId);

        // 2) Add closing note
        if (req.getClosingNotes() != null && !req.getClosingNotes().isEmpty()) {
            logger.debug("Adding closing note to escalation id: {}", escalationId);
            EscalationNote note = new EscalationNote();
            note.setEscalation(saved);
            // map closedBy → createdBy int if you have user ids, or keep null / system id
            note.setCreatedBy(null);
            note.setNoteType("CLOSING_NOTE");
            note.setNote(req.getClosingNotes());
            escalationNoteRepository.save(note);
        }

        // 3) Audit
        auditService.log(
                saved.getEscalationId(),
                "USER",
                null,              // map closedBy to int if needed
                "Escalation Closed",
                req                // contains impact + closingNotes + resolutionStatus
        );

        logger.info("Successfully closed escalation id: {}", escalationId);
        return saved;
    }

}
