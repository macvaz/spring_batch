package com.bde.adminprocessing.web.dto;

import com.bde.adminprocessing.domain.InputDocument;
import com.bde.adminprocessing.domain.enums.DocumentStatus;

import java.time.Instant;
import java.util.List;

public record InputDocumentResponse(
        Long id,
        String documentCode,
        String title,
        String citizenId,
        String procedureCode,
        String procedureName,
        DocumentStatus status,
        Instant receivedAt,
        Instant processedAt,
        List<OutputDocumentResponse> outputs) {

    public static InputDocumentResponse from(InputDocument input) {
        return new InputDocumentResponse(
                input.getId(),
                input.getDocument().getDocumentCode(),
                input.getDocument().getTitle(),
                input.getCitizenId(),
                input.getProcedure().getExternalCode(),
                input.getProcedure().getName(),
                input.getStatus(),
                input.getReceivedAt(),
                input.getProcessedAt(),
                input.getOutputDocuments().stream()
                        .map(OutputDocumentResponse::from)
                        .toList());
    }
}
