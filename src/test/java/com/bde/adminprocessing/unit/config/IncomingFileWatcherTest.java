package com.bde.adminprocessing.unit.config;

import com.bde.adminprocessing.blocked.BlockedOperationImportService;
import com.bde.adminprocessing.config.IncomingFileWatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class IncomingFileWatcherTest {

    @Mock
    private BlockedOperationImportService blockedOperationImportService;

    private IncomingFileWatcher watcher;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        watcher = new IncomingFileWatcher(blockedOperationImportService);
        ReflectionTestUtils.setField(watcher, "incomingDir", tempDir);
        ReflectionTestUtils.setField(watcher, "processedDir", tempDir.resolve("processed"));
        ReflectionTestUtils.setField(watcher, "failedDir", tempDir.resolve("failed"));
        ReflectionTestUtils.setField(watcher, "blockedOperationsFilename", "blocked_operations.csv");
    }

    @Test
    void processesBlockedOperationsFile() throws Exception {
        Path blockedFile = tempDir.resolve("blocked_operations.csv");
        Files.writeString(blockedFile, "header\n");

        watcher.pollIncomingFiles();

        verify(blockedOperationImportService).importFromFile(blockedFile);
    }

    @Test
    void ignoresOtherCsvFiles() throws Exception {
        Path otherFile = tempDir.resolve("submissions.csv");
        Files.writeString(otherFile, "header\n");

        watcher.pollIncomingFiles();

        verifyNoInteractions(blockedOperationImportService);
    }
}
