package com.bde.adminprocessing.web;

import com.bde.adminprocessing.domain.CreditOperationBlocked;
import com.bde.adminprocessing.domain.enums.CreditBlockStatus;
import com.bde.adminprocessing.repository.CreditOperationBlockedRepository;
import com.bde.adminprocessing.web.dto.CreditOperationBlockedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/credit-operations/blocked")
@RequiredArgsConstructor
public class CreditOperationBlockedController {

    private final CreditOperationBlockedRepository creditOperationBlockedRepository;

    @GetMapping("/{id}")
    public ResponseEntity<CreditOperationBlockedResponse> getById(@PathVariable Long id) {
        return creditOperationBlockedRepository.findByIdWithDetails(id)
                .map(CreditOperationBlockedResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-operation/{operationId}")
    public ResponseEntity<CreditOperationBlockedResponse> getByOperationId(@PathVariable String operationId) {
        return creditOperationBlockedRepository.findByOperationId(operationId)
                .flatMap(blocked -> creditOperationBlockedRepository.findByIdWithDetails(blocked.getId()))
                .map(CreditOperationBlockedResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-owner/{ownerId}")
    public List<CreditOperationBlockedResponse> listByOwner(@PathVariable Long ownerId) {
        return creditOperationBlockedRepository.findByOwnerIdWithDetails(ownerId).stream()
                .map(CreditOperationBlockedResponse::from)
                .toList();
    }

    @GetMapping
    public List<CreditOperationBlockedResponse> listByStatus(
            @RequestParam(required = false) CreditBlockStatus status) {
        List<CreditOperationBlocked> blocks = status == null
                ? creditOperationBlockedRepository.findAllWithDetails()
                : creditOperationBlockedRepository.findByBlockStatusWithDetails(status);
        return blocks.stream()
                .map(CreditOperationBlockedResponse::from)
                .toList();
    }
}
