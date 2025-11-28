package com.escalation.app.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class AuditEventResponse {

    private Long auditId;
    private String title;          // e.g. "Escalation Created", "Document Uploaded"
    private String actorLabel;     // e.g. "System Auto-Detection", "Dr. Sarah Wilson"
    private String description;    // multi-line text under each event
    private String createdAt;      // formatted date/time string
}
