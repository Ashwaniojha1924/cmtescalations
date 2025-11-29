package com.escalation.app.service;

import com.escalation.app.entity.Escalation;
import com.escalation.app.entity.EscalationNote;
import com.escalation.app.exception.ResourceNotFoundException;
import com.escalation.app.repository.EscalationNoteRepository;
import com.escalation.app.repository.EscalationRepository;
import com.escalation.app.response.EscalationOverviewResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EscalationQueryService {

    private static final Logger logger = LoggerFactory.getLogger(EscalationQueryService.class);

    private final EscalationRepository escalationRepository;
    private final EscalationNoteRepository escalationNoteRepository;
    // plus PatientRepository / DeviceRepository if you have them

    public EscalationOverviewResponse getOverview(String patientId, String admissionId) {
        logger.debug("Fetching escalation overview for patientId: {}, admissionId: {}", patientId, admissionId);

        List<Escalation> escalations =
                escalationRepository.findByPatientIdAndAdmissionId(patientId, admissionId);

        if (escalations.isEmpty()) {
            logger.warn("No escalations found for patientId: {}, admissionId: {}", patientId, admissionId);
            throw new ResourceNotFoundException("Escalations", String.format("patientId: %s, admissionId: %s", patientId, admissionId));
        }
        
        logger.debug("Found {} escalations for patientId: {}, admissionId: {}", escalations.size(), patientId, admissionId);

        EscalationOverviewResponse response = new EscalationOverviewResponse();

        // 1) Patient section – usually from patients table/service
        Escalation any = escalations.get(0);
        EscalationOverviewResponse.PatientSection ps = new EscalationOverviewResponse.PatientSection();
        ps.setPatientName("John Anderson");             // fetch from patient entity/service
        ps.setPatientId(any.getPatientId());
        ps.setAge("67 years");
        ps.setGender("Male");
        ps.setHospitalName("City General Hospital");
        ps.setHospitalDiagnosis("Acute Coronary Syndrome, Hypertension");
        ps.setAlertDateTime(any.getCreatedAt().toString());
        ps.setPatchDeploymentDate("2025-01-12 08:00");  // example
        ps.setPatchDeviceId("BS-4569-7821");
        response.setPatient(ps);

        // 2) Device section – from device table/service if you have one
        EscalationOverviewResponse.DeviceSection ds = new EscalationOverviewResponse.DeviceSection();
        ds.setDeviceName("Bio-Sensor ID BS-4569-7821");
        ds.setDeviceModel("LifePatch Pro v2.3");
        ds.setDeploymentDate("2025-01-12 08:00");
        ds.setBatteryLevel("78%");
        response.setDevice(ds);

        // 3) Big escalation notes summary – can come from escalation.summary or notes
        response.setEscalationNotesSummary(any.getSummary());

        // 4) Alert cards – 1 card per escalation row
        List<EscalationOverviewResponse.AlertCard> cards = new java.util.ArrayList<>();
        for (Escalation e : escalations) {
            EscalationOverviewResponse.AlertCard card = new EscalationOverviewResponse.AlertCard();
            card.setEscalationId(e.getEscalationId());
            card.setAlertId(e.getAlertId());

            // These can come from alertId/specification mapping or another service:
            card.setMetricName("RESPIRATION RATE");  // example
            card.setCurrentValue("19");
            card.setUnit("BRPM");
            card.setStatus(e.getPriority());         // HIGH / MEDIUM / LOW
            card.setRangeLabel("8.0 - 40");

            if (e.getSpecification() != null) {
                card.setSpecification(e.getSpecification().getCode());
            }

            // combine all escalation_notes for this escalation into one notes string
            String notes = escalationNoteRepository.findByEscalation_EscalationId(e.getEscalationId())
                    .stream()
                    .map(EscalationNote::getNote)
                    .collect(Collectors.joining("\n"));
            card.setNotes(notes);

            cards.add(card);
        }
        response.setAlerts(cards);

        logger.info("Successfully built overview with {} alert cards for patientId: {}, admissionId: {}", cards.size(), patientId, admissionId);
        return response;
    }
}
