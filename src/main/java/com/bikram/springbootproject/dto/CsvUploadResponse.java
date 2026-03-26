package com.bikram.springbootproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class CsvUploadResponse {
    private int totalRows;
    private int savedCount;
    private int failedCount;
    private List<CsvRowError> errors;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CsvRowError {
        private long rowNumber;
        private String field;
        private String message;
    }
}
