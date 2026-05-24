package com.bde.adminprocessing.web;

import com.bde.adminprocessing.repository.InputDocumentRepository;
import com.bde.adminprocessing.repository.OutputDocumentRepository;
import com.bde.adminprocessing.web.dto.InputDocumentResponse;
import com.bde.adminprocessing.web.dto.OutputDocumentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final InputDocumentRepository inputDocumentRepository;
    private final OutputDocumentRepository outputDocumentRepository;

    @GetMapping("/inputs/{id}")
    public ResponseEntity<InputDocumentResponse> getInputDocument(@PathVariable Long id) {
        return inputDocumentRepository.findByIdWithOutputs(id)
                .map(InputDocumentResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/inputs/by-code/{documentCode}")
    public ResponseEntity<InputDocumentResponse> getInputByCode(@PathVariable String documentCode) {
        return inputDocumentRepository.findByDocumentDocumentCode(documentCode)
                .flatMap(input -> inputDocumentRepository.findByIdWithOutputs(input.getId()))
                .map(InputDocumentResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/outputs/{id}")
    public ResponseEntity<OutputDocumentResponse> getOutputDocument(@PathVariable Long id) {
        return outputDocumentRepository.findByIdWithDetails(id)
                .map(OutputDocumentResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/inputs/{inputId}/outputs")
    public ResponseEntity<List<OutputDocumentResponse>> listOutputsForInput(@PathVariable Long inputId) {
        List<OutputDocumentResponse> outputs = outputDocumentRepository.findByInputDocumentId(inputId).stream()
                .map(OutputDocumentResponse::from)
                .toList();
        return ResponseEntity.ok(outputs);
    }
}
