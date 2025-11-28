package com.escalation.app.response;
    import lombok.Data;
    import lombok.Getter;
    import lombok.Setter;

@Data
@Setter
@Getter
    public class HseReviewResponse {

        private Long escalationId;

        // Header bits (you can expand as needed)
        private String patientName;
        private String patientId;
        private String priority;
        private String hospitalName;
        private String createdAt;
        private String lastUpdatedAt;

        // HSE/HSL Review sections
        private String actionByLifesigns;    // column text 1
        private String actionByMedicalTeam;  // column text 2
        private String clinicalResponse;     // column text 3

        private String reviewStatus;         // e.g. "Valid Escalation Confirmed"
        private String escalationStatus;     // e.g. "OPEN", "CLOSED"
    }

