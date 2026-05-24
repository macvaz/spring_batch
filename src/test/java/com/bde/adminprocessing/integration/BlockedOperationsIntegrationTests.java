package com.bde.adminprocessing.integration;

import com.bde.adminprocessing.blocked.BlockedOperationCsvReader;
import com.bde.adminprocessing.blocked.BlockedOperationImportService;
import com.bde.adminprocessing.blocked.BlockedOperationValidationException;
import com.bde.adminprocessing.blocked.MissingBlockedOperationsFileException;
import com.bde.adminprocessing.repository.CreditOperationBlockedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class BlockedOperationsIntegrationTests {

    @Autowired
    private BlockedOperationCsvReader csvReader;

    @Autowired
    private BlockedOperationImportService blockedOperationImportService;

    @Autowired
    private CreditOperationBlockedRepository creditOperationBlockedRepository;

    @TempDir
    Path tempDir;

    @BeforeEach
    void cleanBlockedOperations() {
        creditOperationBlockedRepository.deleteAll();
    }

    @Test
    void throwsWhenBlockedOperationsFileIsMissing() {
        Path missing = tempDir.resolve("blocked_operations.csv");

        assertThatThrownBy(() -> csvReader.read(missing))
                .isInstanceOf(MissingBlockedOperationsFileException.class)
                .hasMessageContaining("blocked_operations.csv");
    }

    @Test
    void readsValidCsvFixture() throws Exception {
        Path fixture = classpathResource("blocked_operations_valid.csv");

        var records = csvReader.read(fixture);

        assertThat(records).hasSize(3);
        assertThat(records.get(0).getOperationId()).isEqualTo("OP-2026-001");
        assertThat(records.get(0).getOwnerId()).isEqualTo("12345678A");
        assertThat(records.get(0).getEntityId()).isEqualTo("BANK-001");
    }

    @Test
    void importsValidBlockedOperations() throws Exception {
        int imported = blockedOperationImportService.importFromFile(
                classpathResource("blocked_operations_valid.csv"));

        assertThat(imported).isEqualTo(3);
        assertThat(creditOperationBlockedRepository.findByOperationIdWithDetails("OP-2026-001"))
                .isPresent()
                .get()
                .satisfies(block -> {
                    assertThat(block.getOwner().getNationalId()).isEqualTo("12345678A");
                    assertThat(block.getBankingEntity().getExternalCode()).isEqualTo("BANK-001");
                    assertThat(block.getBlockMonth()).isEqualTo("2026-01");
                });
    }

    @Test
    void rejectsInvalidBlockedOperationsFile() throws Exception {
        long countBefore = creditOperationBlockedRepository.count();

        assertThatThrownBy(() -> blockedOperationImportService.importFromFile(
                classpathResource("blocked_operations_invalid.csv")))
                .isInstanceOf(BlockedOperationValidationException.class)
                .satisfies(ex -> {
                    var errors = ((BlockedOperationValidationException) ex).getErrors();
                    assertThat(errors).anyMatch(msg -> msg.contains("owner_id not found in credit_owners_information"));
                    assertThat(errors).anyMatch(msg -> msg.contains("entity_id not found in banking_entity"));
                    assertThat(errors).anyMatch(msg -> msg.contains("block_month must match YYYY-MM"));
                    assertThat(errors).anyMatch(msg -> msg.contains("blocked_amount is not a valid number"));
                    assertThat(errors).anyMatch(msg -> msg.contains("block_status is invalid"));
                });

        assertThat(creditOperationBlockedRepository.count()).isEqualTo(countBefore);
        assertThat(creditOperationBlockedRepository.findByOperationId("OP-INVALID-001")).isEmpty();
    }

    @Test
    void rejectsWhenOperationIdAlreadyExists() throws Exception {
        blockedOperationImportService.importFromFile(classpathResource("blocked_operations_valid.csv"));

        assertThatThrownBy(() -> blockedOperationImportService.importFromFile(
                classpathResource("blocked_operations_duplicate.csv")))
                .isInstanceOf(BlockedOperationValidationException.class)
                .satisfies(ex -> assertThat(((BlockedOperationValidationException) ex).getErrors())
                        .anyMatch(msg -> msg.contains("operation_id already exists")));
    }

    @Test
    void missingFileDoesNotPersistRows() {
        Path missing = tempDir.resolve("blocked_operations.csv");

        assertThatThrownBy(() -> blockedOperationImportService.importFromFile(missing))
                .isInstanceOf(MissingBlockedOperationsFileException.class);

        assertThat(creditOperationBlockedRepository.count()).isZero();
    }

    private Path classpathResource(String name) throws Exception {
        return Path.of(getClass().getClassLoader().getResource(name).toURI());
    }
}
