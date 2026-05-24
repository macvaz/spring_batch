package com.bde.adminprocessing.config;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class SourceFileStepListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        String sourceFileName = stepExecution.getJobParameters().getString("sourceFileName", "unknown.csv");
        stepExecution.getExecutionContext().putString("sourceFileName", sourceFileName);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return stepExecution.getExitStatus();
    }
}
