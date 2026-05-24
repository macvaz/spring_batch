package com.bde.adminprocessing.service;

import com.bde.adminprocessing.domain.Document;
import com.bde.adminprocessing.domain.InputDocument;
import com.bde.adminprocessing.domain.OutputDocument;
import com.bde.adminprocessing.domain.Procedure;
import com.bde.adminprocessing.domain.enums.DocumentStatus;
import com.bde.adminprocessing.domain.enums.ProcedureStatus;
import com.bde.adminprocessing.integration.DigitalSignatureClient;
import com.bde.adminprocessing.integration.NotificationClient;
import com.bde.adminprocessing.integration.NotificationResult;
import com.bde.adminprocessing.integration.SignatureResult;
import com.bde.adminprocessing.repository.InputDocumentRepository;
import com.bde.adminprocessing.repository.OutputDocumentRepository;
import com.bde.adminprocessing.repository.ProcedureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ElectronicProcessingService {

    private final DigitalSignatureClient digitalSignatureClient;
    private final NotificationClient notificationClient;
    private final InputDocumentRepository inputDocumentRepository;
    private final OutputDocumentRepository outputDocumentRepository;
    private final ProcedureRepository procedureRepository;

    @Transactional
    public OutputDocument process(InputDocument inputDocument) {
        inputDocument.setStatus(DocumentStatus.PROCESSING);
        inputDocumentRepository.save(inputDocument);

        try {
            SignatureResult signature = digitalSignatureClient.sign(inputDocument).block();

            Document outputDocEntity = Document.builder()
                    .procedure(inputDocument.getProcedure())
                    .documentCode(inputDocument.getDocument().getDocumentCode() + "-OUT")
                    .title(inputDocument.getDocument().getTitle() + " (processed)")
                    .mimeType(inputDocument.getDocument().getMimeType())
                    .contentReference(signature.signedContentReference())
                    .checksum(inputDocument.getDocument().getChecksum())
                    .build();

            OutputDocument outputDocument = OutputDocument.builder()
                    .document(outputDocEntity)
                    .inputDocument(inputDocument)
                    .signatureReference(signature.reference())
                    .status(DocumentStatus.SIGNED)
                    .build();

            outputDocument = outputDocumentRepository.save(outputDocument);

            NotificationResult notification = notificationClient
                    .notifyCitizen(inputDocument, outputDocument)
                    .block();

            outputDocument.setNotificationReference(notification.reference());
            outputDocument.setDeliveryChannel(notification.deliveryChannel());
            outputDocument.setStatus(DocumentStatus.READY);
            outputDocument = outputDocumentRepository.save(outputDocument);

            inputDocument.setStatus(DocumentStatus.READY);
            inputDocument.setProcessedAt(Instant.now());
            inputDocumentRepository.save(inputDocument);

            updateProcedureIfComplete(inputDocument.getProcedure());

            return outputDocument;
        } catch (Exception ex) {
            inputDocument.setStatus(DocumentStatus.FAILED);
            inputDocument.setErrorMessage(ex.getMessage());
            inputDocumentRepository.save(inputDocument);
            throw ex;
        }
    }

    private void updateProcedureIfComplete(Procedure procedure) {
        long pending = inputDocumentRepository.findByProcedureIdAndStatusNot(
                procedure.getId(), DocumentStatus.READY).size();
        if (pending == 0) {
            procedure.setStatus(ProcedureStatus.COMPLETED);
            procedureRepository.save(procedure);
        }
    }
}
