package com.escalation.app.repository;

import com.escalation.app.entity.HseReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HseReviewRepository extends JpaRepository<HseReview, Long> {
    Optional<HseReview> findByEscalation_EscalationId(Long escalationId);
}