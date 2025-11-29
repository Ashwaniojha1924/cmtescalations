package com.escalation.app.controller;

import com.escalation.app.response.AuditEventResponse;
import com.escalation.app.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/escalations")
@RequiredArgsConstructor
public class AuditTrailController {

    private static final Logger logger = LoggerFactory.getLogger(AuditTrailController.class);

    private final AuditService auditTrailService;

    // GET /api/escalations/{id}/audit-trail
    @GetMapping("/{id}/audit-trail")
    public List<AuditEventResponse> getAuditTrail(@PathVariable("id") Long escalationId) {
        logger.info("Fetching audit trail for escalation id: {}", escalationId);
        List<AuditEventResponse> auditTrail = auditTrailService.getAuditForEscalation(escalationId);
        logger.debug("Retrieved {} audit events for escalation id: {}", auditTrail.size(), escalationId);
        return auditTrail;
    }
}
