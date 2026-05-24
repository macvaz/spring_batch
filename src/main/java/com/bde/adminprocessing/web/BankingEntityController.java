package com.bde.adminprocessing.web;

import com.bde.adminprocessing.repository.BankingEntityRepository;
import com.bde.adminprocessing.web.dto.BankingEntityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/banking-entities")
@RequiredArgsConstructor
public class BankingEntityController {

    private final BankingEntityRepository bankingEntityRepository;

    @GetMapping
    public List<BankingEntityResponse> listAll() {
        return bankingEntityRepository.findAll().stream()
                .map(BankingEntityResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BankingEntityResponse> getById(@PathVariable Long id) {
        return bankingEntityRepository.findById(id)
                .map(BankingEntityResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-code/{code}")
    public ResponseEntity<BankingEntityResponse> getByCode(@PathVariable String code) {
        return bankingEntityRepository.findByExternalCode(code)
                .map(BankingEntityResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
