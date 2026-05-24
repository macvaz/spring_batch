package com.bde.adminprocessing.blocked;

import com.bde.adminprocessing.domain.enums.CreditBlockStatus;
import com.bde.adminprocessing.repository.BankingEntityRepository;
import com.bde.adminprocessing.repository.CreditOperationBlockedRepository;
import com.bde.adminprocessing.repository.CreditOwnerInformationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class BlockedOperationReferenceValidator {

    private static final Pattern BLOCK_MONTH_PATTERN = Pattern.compile("\\d{4}-\\d{2}");

    private final BankingEntityRepository bankingEntityRepository;
    private final CreditOwnerInformationRepository creditOwnerInformationRepository;
    private final CreditOperationBlockedRepository creditOperationBlockedRepository;

    public List<String> validate(BlockedOperationCsvRecord record, int lineNumber) {
        List<String> errors = new ArrayList<>();
        String prefix = "Line " + lineNumber + ": ";

        if (!StringUtils.hasText(record.getOperationId())) {
            errors.add(prefix + "operation_id is required");
        } else if (creditOperationBlockedRepository.findByOperationId(record.getOperationId()).isPresent()) {
            errors.add(prefix + "operation_id already exists: " + record.getOperationId());
        }

        if (!StringUtils.hasText(record.getOwnerId())) {
            errors.add(prefix + "owner_id is required");
        } else if (creditOwnerInformationRepository.findByNationalId(record.getOwnerId()).isEmpty()) {
            errors.add(prefix + "owner_id not found in credit_owners_information: " + record.getOwnerId());
        }

        if (!StringUtils.hasText(record.getEntityId())) {
            errors.add(prefix + "entity_id is required");
        } else if (bankingEntityRepository.findByExternalCode(record.getEntityId()).isEmpty()) {
            errors.add(prefix + "entity_id not found in banking_entity: " + record.getEntityId());
        }

        if (StringUtils.hasText(record.getBlockMonth())
                && !BLOCK_MONTH_PATTERN.matcher(record.getBlockMonth()).matches()) {
            errors.add(prefix + "block_month must match YYYY-MM, got: " + record.getBlockMonth());
        }

        if (StringUtils.hasText(record.getBlockedAmount())) {
            try {
                new BigDecimal(record.getBlockedAmount());
            } catch (NumberFormatException ex) {
                errors.add(prefix + "blocked_amount is not a valid number: " + record.getBlockedAmount());
            }
        }

        if (StringUtils.hasText(record.getBlockStatus())) {
            try {
                CreditBlockStatus.valueOf(record.getBlockStatus());
            } catch (IllegalArgumentException ex) {
                errors.add(prefix + "block_status is invalid: " + record.getBlockStatus());
            }
        }

        return errors;
    }
}
