package com.escalation.app.controller;

import com.escalation.app.entity.*;
import com.escalation.app.repository.AuditTrailRepository;
import com.escalation.app.request.CloseEscalationRequest;
import com.escalation.app.request.EscalationCreateRequest;
import com.escalation.app.request.ReEscalateRequest;
import com.escalation.app.response.EscalationOverviewResponse;
import com.escalation.app.service.EscalationQueryService;
import com.escalation.app.service.EscalationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/escalations")
@RequiredArgsConstructor
public class EscalationController {

    private static final Logger logger = LoggerFactory.getLogger(EscalationController.class);

    private final EscalationService escalationService;
    private final EscalationQueryService escalationQueryService;
    private final AuditTrailRepository auditTrailRepository;

    @PostMapping
    public Escalation createEscalation(@RequestBody EscalationCreateRequest request) {
        logger.info("Creating escalation for patientId: {}, admissionId: {}", request.getPatientId(), request.getAdmissionId());
        try {
            Escalation escalation = escalationService.createEscalationWithChildren(request);
            logger.info("Successfully created escalation with id: {}", escalation.getEscalationId());
            return escalation;
        } catch (Exception e) {
            logger.error("Error creating escalation for patientId: {}, admissionId: {}", request.getPatientId(), request.getAdmissionId(), e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public Escalation getEscalation(@PathVariable Long id) {
        logger.info("Fetching escalation with id: {}", id);
        try {
            Escalation escalation = escalationService.getEscalation(id);
            logger.debug("Successfully retrieved escalation with id: {}", id);
            return escalation;
        } catch (Exception e) {
            logger.error("Error fetching escalation with id: {}", id, e);
            throw e;
        }
    }

    @GetMapping("/{id}/audit")
    public List<AuditTrail> getAudit(@PathVariable Long id) {
        logger.info("Fetching audit trail for escalation id: {}", id);
        try {
            List<AuditTrail> auditTrails = escalationService.getAuditForEscalation(id, auditTrailRepository);
            logger.debug("Retrieved {} audit trail entries for escalation id: {}", auditTrails.size(), id);
            return auditTrails;
        } catch (Exception e) {
            logger.error("Error fetching audit trail for escalation id: {}", id, e);
            throw e;
        }
    }


    @GetMapping("/overview")
    public EscalationOverviewResponse getOverview(
            @RequestParam String patientId,
            @RequestParam String admissionId) {
        logger.info("Fetching escalation overview for patientId: {}, admissionId: {}", patientId, admissionId);
        try {
            EscalationOverviewResponse overview = escalationQueryService.getOverview(patientId, admissionId);
            logger.debug("Successfully retrieved overview for patientId: {}, admissionId: {}", patientId, admissionId);
            return overview;
        } catch (Exception e) {
            logger.error("Error fetching overview for patientId: {}, admissionId: {}", patientId, admissionId, e);
            throw e;
        }
    }

    @PostMapping("/{id}/re-escalate")
    public Escalation reEscalate(
            @PathVariable Long id,
            @RequestBody ReEscalateRequest request) {
        logger.info("Re-escalating escalation id: {} with new priority: {}", id, request.getPriority());
        try {
            Escalation escalation = escalationService.reEscalate(id, request);
            logger.info("Successfully re-escalated. New escalation id: {}", escalation.getEscalationId());
            return escalation;
        } catch (Exception e) {
            logger.error("Error re-escalating escalation id: {}", id, e);
            throw e;
        }
    }

    @PostMapping("/{id}/close")
    public Escalation closeEscalation(
            @PathVariable Long id,
            @RequestBody CloseEscalationRequest request) {
        logger.info("Closing escalation id: {} by user: {}", id, request.getClosedBy());
        try {
            Escalation escalation = escalationService.closeEscalation(id, request);
            logger.info("Successfully closed escalation id: {}", id);
            return escalation;
        } catch (Exception e) {
            logger.error("Error closing escalation id: {}", id, e);
            throw e;
        }
    }

   /* @PostMapping("/{id}/vitals")
    public EscalationVital addVital(
            @PathVariable Long id,
            @RequestBody EscalationVital vital,
            @RequestParam(value = "specificationId", required = false) Long specificationId) {

        return escalationService.addVital(id, vital, specificationId);
    }

    @PostMapping("/{id}/notes")
    public EscalationNote addNote(
            @PathVariable Long id,
            @RequestBody EscalationNote note) {

        return escalationService.addNote(id, note);
    }

    @PostMapping("/{id}/documents")
    public Document addDocument(
            @PathVariable Long id,
            @RequestBody Document document) {

        return escalationService.addDocument(id, document);
    }*/
}
