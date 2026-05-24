package com.bde.adminprocessing.web;

import com.bde.adminprocessing.repository.ProcedureRepository;
import com.bde.adminprocessing.web.dto.ProcedureResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/procedures")
@RequiredArgsConstructor
public class ProcedureController {

    private final ProcedureRepository procedureRepository;

    @GetMapping
    public List<ProcedureResponse> listProcedures() {
        return procedureRepository.findAll().stream()
                .map(ProcedureResponse::from)
                .toList();
    }

    @GetMapping("/{code}")
    public ResponseEntity<ProcedureResponse> getByCode(@PathVariable String code) {
        return procedureRepository.findByExternalCode(code)
                .map(ProcedureResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
