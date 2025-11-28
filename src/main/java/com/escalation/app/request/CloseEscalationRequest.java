package com.escalation.app.request;

import lombok.Data;

@Data
public class CloseEscalationRequest {

    private String closedBy;          // user id / name of the closer
    private String closingNotes;      // free text (Clinical Response / closure reason)
    private String resolutionStatus;
    private String impact; // e.g. "RESOLVED", "NO_ACTION_REQUIRED", etc. (optional)
}
