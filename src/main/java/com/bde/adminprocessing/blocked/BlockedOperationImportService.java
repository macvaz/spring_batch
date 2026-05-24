package com.bde.adminprocessing.blocked;

import com.bde.adminprocessing.domain.CreditOperationBlocked;
import com.bde.adminprocessing.domain.enums.CreditBlockStatus;
import com.bde.adminprocessing.repository.BankingEntityRepository;
import com.bde.adminprocessing.repository.CreditOperationBlockedRepository;
import com.bde.adminprocessing.repository.CreditOwnerInformationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlockedOperationImportService {

    private final BlockedOperationCsvReader csvReader;
    private final BlockedOperationReferenceValidator referenceValidator;
    private final BankingEntityRepository bankingEntityRepository;
    private final CreditOwnerInformationRepository creditOwnerInformationRepository;
    private final CreditOperationBlockedRepository creditOperationBlockedRepository;

    @Transactional
    public int importFromFile(Path csvFile) {
        List<BlockedOperationCsvRecord> records = csvReader.read(csvFile);
        List<String> allErrors = new ArrayList<>();

        int lineNumber = 1;
        for (BlockedOperationCsvRecord record : records) {
            lineNumber++;
            allErrors.addAll(referenceValidator.validate(record, lineNumber));
        }

        if (!allErrors.isEmpty()) {
            throw new BlockedOperationValidationException(allErrors);
        }

        int imported = 0;
        for (BlockedOperationCsvRecord record : records) {
            creditOperationBlockedRepository.save(mapToEntity(record));
            imported++;
        }
        return imported;
    }

    private CreditOperationBlocked mapToEntity(BlockedOperationCsvRecord record) {
        var owner = creditOwnerInformationRepository.findByNationalId(record.getOwnerId()).orElseThrow();
        var bankingEntity = bankingEntityRepository.findByExternalCode(record.getEntityId()).orElseThrow();

        return CreditOperationBlocked.builder()
                .operationId(record.getOperationId())
                .owner(owner)
                .bankingEntity(bankingEntity)
                .blockMonth(blankToNull(record.getBlockMonth()))
                .blockedAmount(StringUtils.hasText(record.getBlockedAmount())
                        ? new BigDecimal(record.getBlockedAmount())
                        : null)
                .currency(blankToNull(record.getCurrency()))
                .blockReason(blankToNull(record.getBlockReason()))
                .blockStatus(StringUtils.hasText(record.getBlockStatus())
                        ? CreditBlockStatus.valueOf(record.getBlockStatus())
                        : CreditBlockStatus.ACTIVE)
                .externalReference(blankToNull(record.getExternalReference()))
                .build();
    }

    private String blankToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
