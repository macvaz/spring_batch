package com.bde.adminprocessing.integration;

import com.bde.adminprocessing.repository.BankingEntityRepository;
import com.bde.adminprocessing.repository.CreditOperationBlockedRepository;
import com.bde.adminprocessing.repository.CreditOwnerInformationRepository;
import com.bde.adminprocessing.repository.InputDocumentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LoadSeedDataIntoDatabaseIntegrationTest {

    @Autowired
    private BankingEntityRepository bankingEntityRepository;

    @Autowired
    private CreditOwnerInformationRepository creditOwnerInformationRepository;

    @Autowired
    private CreditOperationBlockedRepository creditOperationBlockedRepository;

    @Autowired
    private InputDocumentRepository inputDocumentRepository;

    @Test
    void dimensionalDataIsLoadedFromSql() {
        assertThat(bankingEntityRepository.findAll()).hasSize(3);
        assertThat(creditOwnerInformationRepository.findAll()).hasSize(3);
        assertThat(creditOperationBlockedRepository.count()).isZero();
        assertThat(inputDocumentRepository.findByDocumentDocumentCode("DOC-T-001")).isPresent();

        assertThat(bankingEntityRepository.findByExternalCode("BANK-001")).isPresent();

        Long docT001InputId = inputDocumentRepository.findByDocumentDocumentCode("DOC-T-001")
                .orElseThrow()
                .getId();
        assertThat(creditOwnerInformationRepository.findByNationalIdAndInputDocumentId(
                "12345678A", docT001InputId)).isPresent();
    }
}
