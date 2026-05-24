package com.bde.adminprocessing.web.dto;

import com.bde.adminprocessing.domain.CreditOwnerInformation;
import com.bde.adminprocessing.domain.enums.CreditOwnerRole;

import java.math.BigDecimal;
import java.time.Instant;

public record CreditOwnerInformationResponse(
        Long id,
        Long inputDocumentId,
        String name,
        String nationalId,
        String email,
        String phone,
        String address,
        BigDecimal ownershipPercentage,
        CreditOwnerRole role,
        Instant createdAt) {

    public static CreditOwnerInformationResponse from(CreditOwnerInformation owner) {
        return from(owner, owner.getInputDocument().getId());
    }

    public static CreditOwnerInformationResponse from(CreditOwnerInformation owner, Long inputDocumentId) {
        return new CreditOwnerInformationResponse(
                owner.getId(),
                inputDocumentId,
                owner.getName(),
                owner.getNationalId(),
                owner.getEmail(),
                owner.getPhone(),
                owner.getAddress(),
                owner.getOwnershipPercentage(),
                owner.getRole(),
                owner.getCreatedAt());
    }
}
