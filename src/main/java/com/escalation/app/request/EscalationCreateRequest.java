package com.escalation.app.request;

import lombok.Data;

import java.util.List;


    @Data
    public class EscalationCreateRequest {

        private String patientId;
        private String createdBy;
        private String priority;
        private String status;
       // private String summary;
       // private String notes;
        private String admissionId;
        private String facilityId;
        private String updatedBy;

        // comes as plain values
        private Long alertId;
        private Long specificationId;   // 🔹 NEW

        private List<NoteDto> escalationNotes;
        private List<DocumentDto> documents;
        private List<SpecNoteDto> specifications;

        @Data
        public static class NoteDto {
            private String createdBy;
            private String noteType;
            private String note;
        }

        @Data
        public static class DocumentDto {
            private Integer uploadedBy;
            private String fileName;
            private String fileType;
            private Integer docId;
        }
        @Data
        public static class SpecNoteDto {
            private Long specificationId;
            private String notes;
        }
    }

