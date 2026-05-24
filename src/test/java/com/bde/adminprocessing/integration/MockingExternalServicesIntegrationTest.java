package com.bde.adminprocessing.integration;

import com.bde.adminprocessing.domain.enums.DocumentStatus;
import com.bde.adminprocessing.repository.InputDocumentRepository;
import com.bde.adminprocessing.repository.OutputDocumentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@SpringBatchTest
class MockingExternalServicesIntegrationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private Job documentProcessingJob;

    @Autowired
    private InputDocumentRepository inputDocumentRepository;

    @Autowired
    private OutputDocumentRepository outputDocumentRepository;

    @Test
    void processesSeededInputDocumentsThroughElectronicProcessingStep() {
        assertThat(inputDocumentRepository.findByStatus(DocumentStatus.VALIDATED)).hasSize(2);

        jobLauncherTestUtils.setJob(documentProcessingJob);
        jobLauncherTestUtils.launchStep("electronicProcessingStep", new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters());

        assertThat(inputDocumentRepository.findByStatus(DocumentStatus.READY)).hasSize(2);
        assertThat(outputDocumentRepository.count()).isEqualTo(2);
        assertThat(outputDocumentRepository.countBySignatureReferenceIsNotNull()).isEqualTo(2);

        assertThat(inputDocumentRepository.findByDocumentDocumentCode("DOC-T-001")).isPresent();
        assertThat(outputDocumentRepository.findByInputDocumentId(
                inputDocumentRepository.findByDocumentDocumentCode("DOC-T-001").orElseThrow().getId()))
                .hasSize(1);
    }
}
