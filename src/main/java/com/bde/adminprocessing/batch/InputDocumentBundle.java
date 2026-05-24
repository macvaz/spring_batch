package com.bde.adminprocessing.batch;

import com.bde.adminprocessing.domain.InputDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InputDocumentBundle {
    private InputDocument inputDocument;
    private String sourceFileName;
}
