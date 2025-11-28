package com.escalation.app.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class EscalationOverviewResponse {
    private PatientSection patient;
    private DeviceSection device;
    private String escalationNotesSummary;
    private List<AlertCard> alerts;   // the cards at the bottom

    @Data
    public static class PatientSection {
        private String patientName;
        private String patientId;
        private String age;
        private String gender;
        private String hospitalName;
        private String hospitalDiagnosis;
        private String alertDateTime;
        private String patchDeploymentDate;
        private String patchDeviceId;
    }

    @Data
    public static class DeviceSection {
        private String deviceName;
        private String deviceModel;
        private String deploymentDate;
        private String batteryLevel;
    }

    @Data
    public static class AlertCard {
        private Long escalationId;
        private String metricName;        // e.g. "RESPIRATION RATE"
        private String currentValue;      // e.g. "19"
        private String unit;              // e.g. "BRPM"
        private String status;            // e.g. "HIGH"
        private String rangeLabel;        // e.g. "8.0 - 40"
        private Long alertId;             // from escalation.alertId
        private String specification;     // from escalation.specification (e.g. "Tachypnea")
        private String notes;             // text for the Notes column
    }
}

