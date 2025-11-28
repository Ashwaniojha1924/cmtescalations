package com.escalation.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "escalation_specifications")
@Data
public class EscalationSpecification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "escalation_id")
    @JsonIgnore
    private Escalation escalation;

    @ManyToOne
    @JoinColumn(name = "specification_id")
    private Specification specification;

    @Column(columnDefinition = "TEXT")
    private String notes;
}