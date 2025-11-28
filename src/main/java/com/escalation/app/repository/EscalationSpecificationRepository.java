package com.escalation.app.repository;

import com.escalation.app.entity.EscalationSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EscalationSpecificationRepository extends JpaRepository<EscalationSpecification, Long> {

    List<EscalationSpecification> findByEscalation_EscalationId(Long escalationId);
}
