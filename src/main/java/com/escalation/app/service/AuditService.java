package com.escalation.app.service;

import com.escalation.app.entity.AuditTrail;
import com.escalation.app.repository.AuditTrailRepository;
import com.escalation.app.response.AuditEventResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

    private final AuditTrailRepository auditTrailRepository;
    private final ObjectMapper objectMapper;

    public void log(Long escalationId, String actorType, Integer actorId, String action, Object detailsObj) {
        logger.debug("Logging audit event: escalationId={}, actorType={}, action={}", escalationId, actorType, action);
        AuditTrail at = new AuditTrail();
        at.setEscalationId(escalationId);
        at.setCreatedAt(LocalDateTime.now());
        at.setActorType(actorType);
        at.setActorId(actorId);
        at.setAction(action);

        try {
            at.setDetails(objectMapper.writeValueAsString(detailsObj));
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize audit details for escalationId: {}, action: {}", escalationId, action, e);
            at.setDetails("{\"error\":\"Failed to serialize details\"}");
        }

        auditTrailRepository.save(at);
        logger.trace("Saved audit trail entry with id: {} for escalationId: {}", at.getAuditId(), escalationId);
    }

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm a"); // 14 Jan 2025, 10:32 AM

    public List<AuditEventResponse> getAuditForEscalation(Long escalationId) {
        logger.debug("Fetching audit events for escalation id: {}", escalationId);

        List<AuditTrail> events =
                auditTrailRepository.findByEscalationIdOrderByCreatedAtAsc(escalationId);

        List<AuditEventResponse> responses = events.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        
        logger.debug("Retrieved {} audit events for escalation id: {}", responses.size(), escalationId);
        return responses;
    }

    private AuditEventResponse toDto(AuditTrail at) {
        AuditEventResponse dto = new AuditEventResponse();
        dto.setAuditId(at.getAuditId());
        dto.setTitle(at.getAction());   // "Escalation Created", "Document Uploaded"

        // Basic actor label – you can enrich this by looking up user names, etc.
        String actorLabel;
        if ("SYSTEM".equalsIgnoreCase(at.getActorType())) {
            actorLabel = "System";
        } else {
            actorLabel = at.getActorType(); // or resolve from actorId
        }
        dto.setActorLabel(actorLabel);

        // Description: either take "details" as-is, or parse JSON and format nicely.
        dto.setDescription(at.getDetails());

        dto.setCreatedAt(
                at.getCreatedAt() != null ? at.getCreatedAt().format(FORMATTER) : null
        );
        return dto;
    }
}
