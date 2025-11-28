package com.escalation.app.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Setter
@Getter
public class ReEscalateRequest {
    private String createdBy;           // current HSE user
    private String reEscalationNotes;   // textarea contents
    private List<Long> attachmentDocIds; // IDs from documents table (if you upload separately)

    private String priority;            // optional override
    private Long alertId;               // just a value, no table
    private Long specificationId;
    private List<ReEscalateRequest.DocumentDto> documents;


    @Data
    public static class DocumentDto {
        private Integer uploadedBy;
        private String fileName;
        private String fileType;
        private Integer docId;
    }
}