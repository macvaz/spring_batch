package com.bde.adminprocessing.blocked;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class BlockedOperationCsvReader {

    public List<BlockedOperationCsvRecord> read(Path csvFile) {
        if (!Files.exists(csvFile)) {
            throw new MissingBlockedOperationsFileException(csvFile);
        }

        List<BlockedOperationCsvRecord> records = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(csvFile, StandardCharsets.UTF_8)) {
            String header = reader.readLine();
            if (header == null) {
                return records;
            }
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (!StringUtils.hasText(line)) {
                    continue;
                }
                records.add(parseLine(line, lineNumber));
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read blocked operations file: " + csvFile, ex);
        }
        return records;
    }

    private BlockedOperationCsvRecord parseLine(String line, int lineNumber) {
        String[] fields = line.split(",", -1);
        if (fields.length < 9) {
            throw new IllegalArgumentException(
                    "Line " + lineNumber + " must have 9 columns, found " + fields.length);
        }
        return BlockedOperationCsvRecord.builder()
                .operationId(fields[0].trim())
                .ownerId(fields[1].trim())
                .entityId(fields[2].trim())
                .blockMonth(fields[3].trim())
                .blockedAmount(fields[4].trim())
                .currency(fields[5].trim())
                .blockReason(fields[6].trim())
                .blockStatus(fields[7].trim())
                .externalReference(fields[8].trim())
                .build();
    }
}
