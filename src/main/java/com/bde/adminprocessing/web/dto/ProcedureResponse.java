package com.bde.adminprocessing.web.dto;

import com.bde.adminprocessing.domain.Procedure;
import com.bde.adminprocessing.domain.enums.ProcedureStatus;

import java.time.Instant;

public record ProcedureResponse(
        Long id,
        String externalCode,
        String name,
        String administrationUnit,
        ProcedureStatus status,
        Instant createdAt) {

    public static ProcedureResponse from(Procedure procedure) {
        return new ProcedureResponse(
                procedure.getId(),
                procedure.getExternalCode(),
                procedure.getName(),
                procedure.getAdministrationUnit(),
                procedure.getStatus(),
                procedure.getCreatedAt());
    }
}
