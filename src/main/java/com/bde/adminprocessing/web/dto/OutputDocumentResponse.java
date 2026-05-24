package com.bde.adminprocessing.web.dto;

import com.bde.adminprocessing.domain.OutputDocument;
import com.bde.adminprocessing.domain.enums.DocumentStatus;

import java.time.Instant;

public record OutputDocumentResponse(
        Long id,
        String documentCode,
        String title,
        String contentReference,
        String signatureReference,
        String notificationReference,
        String deliveryChannel,
        DocumentStatus status,
        Instant producedAt) {

    public static OutputDocumentResponse from(OutputDocument output) {
        return new OutputDocumentResponse(
                output.getId(),
                output.getDocument().getDocumentCode(),
                output.getDocument().getTitle(),
                output.getDocument().getContentReference(),
                output.getSignatureReference(),
                output.getNotificationReference(),
                output.getDeliveryChannel(),
                output.getStatus(),
                output.getProducedAt());
    }
}
