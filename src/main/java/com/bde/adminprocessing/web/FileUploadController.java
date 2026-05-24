package com.bde.adminprocessing.web;

import com.bde.adminprocessing.blocked.BlockedOperationImportService;
import com.bde.adminprocessing.web.dto.JobLaunchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final BlockedOperationImportService blockedOperationImportService;

    @Value("${app.storage.incoming}")
    private String incomingDir;

    @Value("${app.storage.blocked-operations-filename:blocked_operations.csv}")
    private String blockedOperationsFilename;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<JobLaunchResponse> uploadBlockedOperations(@RequestParam("file") MultipartFile file)
            throws Exception {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new JobLaunchResponse("Empty file", null));
        }

        if (!blockedOperationsFilename.equalsIgnoreCase(file.getOriginalFilename())) {
            return ResponseEntity.badRequest().body(new JobLaunchResponse(
                    "Expected file name: " + blockedOperationsFilename, file.getOriginalFilename()));
        }

        Path target = Path.of(incomingDir).resolve(file.getOriginalFilename());
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        int imported = blockedOperationImportService.importFromFile(target);
        Files.deleteIfExists(target);

        return ResponseEntity.accepted()
                .body(new JobLaunchResponse("Imported " + imported + " blocked operations", file.getOriginalFilename()));
    }
}
