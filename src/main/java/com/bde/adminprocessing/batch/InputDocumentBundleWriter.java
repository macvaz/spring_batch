package com.bde.adminprocessing.batch;

import com.bde.adminprocessing.domain.InputDocument;
import com.bde.adminprocessing.domain.Procedure;
import com.bde.adminprocessing.domain.enums.ProcedureStatus;
import com.bde.adminprocessing.repository.InputDocumentRepository;
import com.bde.adminprocessing.repository.ProcedureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InputDocumentBundleWriter implements ItemWriter<InputDocumentBundle> {

    private final ProcedureRepository procedureRepository;
    private final InputDocumentRepository inputDocumentRepository;

    @Override
    public void write(Chunk<? extends InputDocumentBundle> chunk) {
        for (InputDocumentBundle bundle : chunk) {
            InputDocument input = bundle.getInputDocument();
            Procedure incoming = input.getProcedure();

            Procedure procedure = procedureRepository.findByExternalCode(incoming.getExternalCode())
                    .orElseGet(() -> procedureRepository.save(incoming));

            if (procedure.getStatus() == ProcedureStatus.OPEN) {
                procedure.setStatus(ProcedureStatus.IN_PROGRESS);
                procedure = procedureRepository.save(procedure);
            }

            input.setProcedure(procedure);
            input.getDocument().setProcedure(procedure);
            inputDocumentRepository.save(input);
        }
    }
}
