package com.escalation.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "hse_review")
@Data
@Setter
@Getter
public class HseReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @OneToOne
    @JoinColumn(name = "escalation_id", unique = true)
    private Escalation escalation;

    @Column(name = "action_by_lifesigns", columnDefinition = "TEXT")
    private String actionByLifesigns;

    @Column(name = "action_by_medical_team", columnDefinition = "TEXT")
    private String actionByMedicalTeam;

    @Column(name = "clinical_response", columnDefinition = "TEXT")
    private String clinicalResponse;

    @Column(name = "review_status")
    private String reviewStatus; // "Valid Escalation Confirmed", "Pending", etc.
}