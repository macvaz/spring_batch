package com.bde.adminprocessing.config;

import com.bde.adminprocessing.blocked.BlockedOperationImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class IncomingFileWatcher {

    private final BlockedOperationImportService blockedOperationImportService;

    @Value("${app.storage.incoming}")
    private Path incomingDir;

    @Value("${app.storage.blocked-operations-filename:blocked_operations.csv}")
    private String blockedOperationsFilename;

    @Value("${app.storage.processed}")
    private Path processedDir;

    @Value("${app.storage.failed}")
    private Path failedDir;

    @Scheduled(fixedDelayString = "${app.watcher.poll-interval-ms:10000}")
    public void pollIncomingFiles() throws Exception {
        if (!Files.isDirectory(incomingDir)) {
            Files.createDirectories(incomingDir);
            return;
        }

        try (Stream<Path> files = Files.list(incomingDir)) {
            files.filter(this::isBlockedOperationsFile)
                    .forEach(this::processFile);
        }
    }

    private void processFile(Path csvFile) {
        try {
            log.info("Processing blocked operations file: {}", csvFile);
            int imported = blockedOperationImportService.importFromFile(csvFile);
            log.info("Imported {} blocked operations from {}", imported, csvFile.getFileName());
            move(csvFile, processedDir);
        } catch (Exception ex) {
            log.error("Failed to process blocked operations file {}", csvFile, ex);
            try {
                move(csvFile, failedDir);
            } catch (IOException moveEx) {
                log.error("Could not move failed file {}", csvFile, moveEx);
            }
        }
    }

    boolean isBlockedOperationsFile(Path csvFile) {
        return csvFile.getFileName().toString().equalsIgnoreCase(blockedOperationsFilename);
    }

    private void move(Path source, Path targetDir) throws IOException {
        Files.createDirectories(targetDir);
        Path target = targetDir.resolve(source.getFileName());
        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
    }
}
