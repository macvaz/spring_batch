package com.bde.adminprocessing.config;

import com.bde.adminprocessing.batch.*;
import com.bde.adminprocessing.domain.InputDocument;
import com.bde.adminprocessing.domain.OutputDocument;
import com.bde.adminprocessing.domain.enums.DocumentStatus;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job documentProcessingJob(
            Step importCsvStep,
            Step electronicProcessingStep) {
        return new JobBuilder("documentProcessingJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(importCsvStep)
                .next(electronicProcessingStep)
                .build();
    }

    @Bean
    public Step importCsvStep(
            FlatFileItemReader<CsvInputRecord> csvReader,
            CsvToInputDocumentProcessor csvProcessor,
            InputDocumentBundleWriter bundleWriter,
            SourceFileStepListener sourceFileStepListener) {
        return new StepBuilder("importCsvStep", jobRepository)
                .<CsvInputRecord, InputDocumentBundle>chunk(50, transactionManager)
                .reader(csvReader)
                .processor(csvProcessor)
                .writer(bundleWriter)
                .listener(sourceFileStepListener)
                .build();
    }

    @Bean
    public Step electronicProcessingStep(
            JpaPagingItemReader<InputDocument> validatedInputReader,
            ElectronicProcessingProcessor electronicProcessingProcessor,
            OutputDocumentWriter outputDocumentWriter) {
        return new StepBuilder("electronicProcessingStep", jobRepository)
                .<InputDocument, OutputDocument>chunk(10, transactionManager)
                .reader(validatedInputReader)
                .processor(electronicProcessingProcessor)
                .writer(outputDocumentWriter)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<CsvInputRecord> csvReader(
            @Value("#{jobParameters['inputFile']}") String inputFile) {
        BeanWrapperFieldSetMapper<CsvInputRecord> mapper = new BeanWrapperFieldSetMapper<>();
        mapper.setTargetType(CsvInputRecord.class);

        return new FlatFileItemReaderBuilder<CsvInputRecord>()
                .name("csvReader")
                .resource(new FileSystemResource(inputFile))
                .delimited()
                .names(
                        "procedureCode",
                        "procedureName",
                        "administrationUnit",
                        "documentCode",
                        "documentTitle",
                        "mimeType",
                        "contentReference",
                        "checksum",
                        "citizenId",
                        "submissionChannel")
                .fieldSetMapper(mapper)
                .linesToSkip(1)
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<InputDocument> validatedInputReader(
            @Value("#{jobParameters['sourceFileName']}") String sourceFileName) {
        var params = new java.util.HashMap<String, Object>();
        params.put("status", DocumentStatus.VALIDATED);

        String query = "SELECT i FROM InputDocument i JOIN FETCH i.document JOIN FETCH i.procedure WHERE i.status = :status";
        if (sourceFileName != null && !sourceFileName.isBlank()) {
            query += " AND i.sourceFile = :sourceFile";
            params.put("sourceFile", sourceFileName);
        }
        query += " ORDER BY i.id";

        return new JpaPagingItemReaderBuilder<InputDocument>()
                .name("validatedInputReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString(query)
                .parameterValues(params)
                .pageSize(20)
                .build();
    }
}
