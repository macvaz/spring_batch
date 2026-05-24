package com.bde.adminprocessing.batch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Row layout expected in incoming CSV files from external applications.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CsvInputRecord {

    private String procedureCode;
    private String procedureName;
    private String administrationUnit;
    private String documentCode;
    private String documentTitle;
    private String mimeType;
    private String contentReference;
    private String checksum;
    private String citizenId;
    private String submissionChannel;
}
