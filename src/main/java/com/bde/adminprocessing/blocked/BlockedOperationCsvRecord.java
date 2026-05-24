package com.bde.adminprocessing.blocked;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockedOperationCsvRecord {

    private String operationId;
    private String ownerId;
    private String entityId;
    private String blockMonth;
    private String blockedAmount;
    private String currency;
    private String blockReason;
    private String blockStatus;
    private String externalReference;
}
