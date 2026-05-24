package com.bde.adminprocessing.web.dto;

import com.bde.adminprocessing.domain.CreditOperationBlocked;
import com.bde.adminprocessing.domain.enums.CreditBlockStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record CreditOperationBlockedResponse(
        Long id,
        String operationId,
        Long ownerId,
        String ownerName,
        String ownerNationalId,
        Long bankingEntityId,
        String bankingEntityCode,
        String bankingEntityName,
        BigDecimal blockedAmount,
        String currency,
        String blockReason,
        CreditBlockStatus blockStatus,
        Instant blockedAt,
        Instant releasedAt,
        String externalReference,
        String blockMonth) {

    public static CreditOperationBlockedResponse from(CreditOperationBlocked blocked) {
        return new CreditOperationBlockedResponse(
                blocked.getId(),
                blocked.getOperationId(),
                blocked.getOwner().getId(),
                blocked.getOwner().getName(),
                blocked.getOwner().getNationalId(),
                blocked.getBankingEntity().getId(),
                blocked.getBankingEntity().getExternalCode(),
                blocked.getBankingEntity().getName(),
                blocked.getBlockedAmount(),
                blocked.getCurrency(),
                blocked.getBlockReason(),
                blocked.getBlockStatus(),
                blocked.getBlockedAt(),
                blocked.getReleasedAt(),
                blocked.getExternalReference(),
                blocked.getBlockMonth());
    }
}
