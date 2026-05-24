package com.bde.adminprocessing.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class BatchJobLauncherService {

    private final JobLauncher jobLauncher;
    private final Job documentProcessingJob;

    public void launchForFile(Path csvFile) throws Exception {
        String fileName = csvFile.getFileName().toString();
        JobParameters parameters = new JobParametersBuilder()
                .addString("inputFile", csvFile.toAbsolutePath().toString())
                .addString("sourceFileName", fileName)
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(documentProcessingJob, parameters);
    }
}
