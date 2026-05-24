package com.bde.adminprocessing.web.dto;

import com.bde.adminprocessing.domain.BankingEntity;

public record BankingEntityResponse(
        Long id,
        String externalCode,
        String name,
        String swiftCode,
        String country) {

    public static BankingEntityResponse from(BankingEntity entity) {
        return new BankingEntityResponse(
                entity.getId(),
                entity.getExternalCode(),
                entity.getName(),
                entity.getSwiftCode(),
                entity.getCountry());
    }
}
