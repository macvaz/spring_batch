package com.bde.adminprocessing.batch;

import com.bde.adminprocessing.domain.Document;
import com.bde.adminprocessing.domain.InputDocument;
import com.bde.adminprocessing.domain.Procedure;
import com.bde.adminprocessing.domain.enums.DocumentStatus;
import com.bde.adminprocessing.domain.enums.ProcedureStatus;
import com.bde.adminprocessing.repository.DocumentRepository;
import com.bde.adminprocessing.repository.InputDocumentRepository;
import com.bde.adminprocessing.repository.ProcedureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@StepScope
@RequiredArgsConstructor
public class CsvToInputDocumentProcessor implements ItemProcessor<CsvInputRecord, InputDocumentBundle> {

    private final ProcedureRepository procedureRepository;
    private final DocumentRepository documentRepository;
    private final InputDocumentRepository inputDocumentRepository;

    @Value("#{jobParameters['sourceFileName']}")
    private String sourceFileName;

    @Override
    public InputDocumentBundle process(CsvInputRecord record) {
        validate(record);

        if (inputDocumentRepository.findByDocumentDocumentCode(record.getDocumentCode()).isPresent()) {
            throw new IllegalStateException("Duplicate document code: " + record.getDocumentCode());
        }

        Procedure procedure = procedureRepository.findByExternalCode(record.getProcedureCode())
                .orElseGet(() -> Procedure.builder()
                        .externalCode(record.getProcedureCode())
                        .name(record.getProcedureName())
                        .administrationUnit(record.getAdministrationUnit())
                        .status(ProcedureStatus.OPEN)
                        .description("Imported administrative procedure")
                        .build());

        Document document = Document.builder()
                .procedure(procedure)
                .documentCode(record.getDocumentCode())
                .title(normalizeTitle(record.getDocumentTitle()))
                .mimeType(record.getMimeType())
                .contentReference(record.getContentReference())
                .checksum(record.getChecksum())
                .build();

        InputDocument inputDocument = InputDocument.builder()
                .document(document)
                .procedure(procedure)
                .citizenId(record.getCitizenId())
                .submissionChannel(record.getSubmissionChannel())
                .sourceFile(sourceFileName)
                .status(DocumentStatus.VALIDATED)
                .build();

        return new InputDocumentBundle(inputDocument, sourceFileName);
    }

    private void validate(CsvInputRecord record) {
        if (!StringUtils.hasText(record.getProcedureCode())) {
            throw new IllegalArgumentException("procedureCode is required");
        }
        if (!StringUtils.hasText(record.getDocumentCode())) {
            throw new IllegalArgumentException("documentCode is required");
        }
        if (!StringUtils.hasText(record.getCitizenId())) {
            throw new IllegalArgumentException("citizenId is required");
        }
        if (!StringUtils.hasText(record.getContentReference())) {
            throw new IllegalArgumentException("contentReference is required");
        }
    }

    private String normalizeTitle(String title) {
        if (!StringUtils.hasText(title)) {
            return "Administrative submission";
        }
        return title.trim();
    }
}
