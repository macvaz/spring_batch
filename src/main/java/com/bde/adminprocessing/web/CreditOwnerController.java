package com.bde.adminprocessing.web;

import com.bde.adminprocessing.repository.CreditOwnerInformationRepository;
import com.bde.adminprocessing.web.dto.CreditOwnerInformationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/credit-owners")
@RequiredArgsConstructor
public class CreditOwnerController {

    private final CreditOwnerInformationRepository creditOwnerInformationRepository;

    @GetMapping("/{id}")
    public ResponseEntity<CreditOwnerInformationResponse> getById(@PathVariable Long id) {
        return creditOwnerInformationRepository.findByIdWithInput(id)
                .map(CreditOwnerInformationResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-input/{inputDocumentId}")
    public List<CreditOwnerInformationResponse> listByInputDocument(@PathVariable Long inputDocumentId) {
        return creditOwnerInformationRepository.findByInputDocumentId(inputDocumentId).stream()
                .map(owner -> CreditOwnerInformationResponse.from(owner, inputDocumentId))
                .toList();
    }
}
