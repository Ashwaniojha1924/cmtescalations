package com.escalation.app.repository;

import com.escalation.app.entity.Escalation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EscalationRepository extends JpaRepository<Escalation, Long> {
    List<Escalation> findByPatientIdAndAdmissionId(String patientId, String admissionId);
}
