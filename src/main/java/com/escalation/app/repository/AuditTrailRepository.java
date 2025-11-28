package com.escalation.app.repository;

import com.escalation.app.entity.AuditTrail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditTrailRepository extends JpaRepository<AuditTrail, Long> {
    List<AuditTrail> findByEscalationIdOrderByCreatedAtAsc(Long escalationId);
}
