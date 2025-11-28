package com.escalation.app.repository;

import com.escalation.app.entity.EscalationNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EscalationNoteRepository extends JpaRepository<EscalationNote, Long> {
    List<EscalationNote> findByEscalation_EscalationId(Long escalationId);
}
