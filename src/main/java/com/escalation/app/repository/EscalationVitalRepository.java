package com.escalation.app.repository;

import com.escalation.app.entity.EscalationVital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EscalationVitalRepository extends JpaRepository<EscalationVital, Long> {
}
