package com.escalation.app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "specifications")
@Data
public class Specification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "specification_id")
    private Long specificationId;

    @Column(nullable = false)
    private String code;

    @Column(name = "sub_code")
    private String subCode;
}
