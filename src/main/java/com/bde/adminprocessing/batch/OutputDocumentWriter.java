package com.bde.adminprocessing.batch;

import com.bde.adminprocessing.domain.OutputDocument;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class OutputDocumentWriter implements ItemWriter<OutputDocument> {

    @Override
    public void write(Chunk<? extends OutputDocument> chunk) {
        // Output documents are persisted by ElectronicProcessingService.
    }
}
