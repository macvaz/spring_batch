package com.bde.adminprocessing.batch;

import com.bde.adminprocessing.domain.InputDocument;
import com.bde.adminprocessing.domain.OutputDocument;
import com.bde.adminprocessing.service.ElectronicProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ElectronicProcessingProcessor implements ItemProcessor<InputDocument, OutputDocument> {

    private final ElectronicProcessingService electronicProcessingService;

    @Override
    public OutputDocument process(InputDocument inputDocument) {
        return electronicProcessingService.process(inputDocument);
    }
}
