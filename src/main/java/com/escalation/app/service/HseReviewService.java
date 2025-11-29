package com.escalation.app.service;

import com.escalation.app.entity.Escalation;
import com.escalation.app.entity.HseReview;
import com.escalation.app.exception.ResourceNotFoundException;
import com.escalation.app.repository.EscalationRepository;
import com.escalation.app.repository.HseReviewRepository;
import com.escalation.app.response.HseReviewResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HseReviewService {

    private static final Logger logger = LoggerFactory.getLogger(HseReviewService.class);

    private final EscalationRepository escalationRepository;
    private final HseReviewRepository hseReviewRepository;

    public HseReviewResponse getHseReview(Long escalationId) {
        logger.debug("Fetching HSE review for escalation id: {}", escalationId);

        Escalation esc = escalationRepository.findById(escalationId)
                .orElseThrow(() -> {
                    logger.error("Escalation not found with id: {}", escalationId);
                    return new ResourceNotFoundException("Escalation", escalationId);
                });

        HseReview review = hseReviewRepository.findByEscalation_EscalationId(escalationId)
                .orElseThrow(() -> {
                    logger.error("HSE Review not found for escalation id: {}", escalationId);
                    return new ResourceNotFoundException("HSE Review", escalationId);
                });

        HseReviewResponse dto = new HseReviewResponse();
        dto.setEscalationId(esc.getEscalationId());
        dto.setPriority(esc.getPriority());
        dto.setEscalationStatus(esc.getStatus());
        dto.setCreatedAt(esc.getCreatedAt().toString());
        dto.setLastUpdatedAt(esc.getLastUpdated().toString());

        // you’d normally pull these from patient/hospital tables:
        dto.setPatientName("John Anderson");
        dto.setPatientId(esc.getPatientId());
        dto.setHospitalName("City General Hospital");

        dto.setActionByLifesigns(review.getActionByLifesigns());
        dto.setActionByMedicalTeam(review.getActionByMedicalTeam());
        dto.setClinicalResponse(review.getClinicalResponse());
        dto.setReviewStatus(review.getReviewStatus());

        logger.debug("Successfully retrieved HSE review for escalation id: {}", escalationId);
        return dto;
    }

    public HseReviewResponse upsertHseReview(Long escalationId, HseReviewResponse req) {
        logger.info("Upserting HSE review for escalation id: {}", escalationId);
        Escalation esc = escalationRepository.findById(escalationId)
                .orElseThrow(() -> {
                    logger.error("Escalation not found with id: {}", escalationId);
                    return new ResourceNotFoundException("Escalation", escalationId);
                });

        HseReview review = hseReviewRepository.findByEscalation_EscalationId(escalationId)
                .orElse(new HseReview());
        review.setEscalation(esc);
        review.setActionByLifesigns(req.getActionByLifesigns());
        review.setActionByMedicalTeam(req.getActionByMedicalTeam());
        review.setClinicalResponse(req.getClinicalResponse());
        review.setReviewStatus(req.getReviewStatus());

        hseReviewRepository.save(review);
        logger.debug("Saved HSE review for escalation id: {}", escalationId);

        logger.info("Successfully upserted HSE review for escalation id: {}", escalationId);
        return getHseReview(escalationId);
    }
}