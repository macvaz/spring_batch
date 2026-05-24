package com.bde.adminprocessing.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class StorageConfig implements ApplicationRunner {

    @Value("${app.storage.incoming}")
    private String incoming;

    @Value("${app.storage.processed}")
    private String processed;

    @Value("${app.storage.failed}")
    private String failed;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Files.createDirectories(Path.of(incoming));
        Files.createDirectories(Path.of(processed));
        Files.createDirectories(Path.of(failed));
    }
}
