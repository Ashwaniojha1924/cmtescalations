package com.escalation.app.controller;

import com.escalation.app.response.HseReviewResponse;
import com.escalation.app.service.HseReviewService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/escalations")
@RequiredArgsConstructor
public class HseReviewController {

    private static final Logger logger = LoggerFactory.getLogger(HseReviewController.class);

    private final HseReviewService hseReviewService;

    // 🌐 GET for loading the HSE Review tab
    @GetMapping("/{id}/hse-review")
    public HseReviewResponse getHseReview(@PathVariable("id") Long escalationId) {
        logger.info("Fetching HSE review for escalation id: {}", escalationId);
        try {
            HseReviewResponse review = hseReviewService.getHseReview(escalationId);
            logger.debug("Successfully retrieved HSE review for escalation id: {}", escalationId);
            return review;
        } catch (Exception e) {
            logger.error("Error fetching HSE review for escalation id: {}", escalationId, e);
            throw e;
        }
    }

    // ✏️ PUT for saving/updating the HSE Review content
    @PutMapping("/{id}/hse-review")
    public HseReviewResponse saveHseReview(
            @PathVariable("id") Long escalationId,
            @RequestBody HseReviewResponse request) {
        logger.info("Saving/updating HSE review for escalation id: {}", escalationId);
        try {
            HseReviewResponse review = hseReviewService.upsertHseReview(escalationId, request);
            logger.info("Successfully saved HSE review for escalation id: {}", escalationId);
            return review;
        } catch (Exception e) {
            logger.error("Error saving HSE review for escalation id: {}", escalationId, e);
            throw e;
        }
    }
}
