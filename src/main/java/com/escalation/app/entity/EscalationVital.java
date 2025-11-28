package com.escalation.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "escalation_vitals")
@Data
public class EscalationVital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "escalation_vital_id")
    private Long escalationVitalId;

    @ManyToOne
    @JoinColumn(name = "escalation_id")
    @JsonIgnore
    private Escalation escalation;

    @Column(name = "alert_id")
    private Integer alertId;

    @Column(name = "measured_value")
    private String measuredValue;

    @Column(name = "measured_at")
    private LocalDateTime measuredAt;

    @ManyToOne
    @JoinColumn(name = "specification_id")
    private Specification specification;

    private String notes;
    private String severity;
}
