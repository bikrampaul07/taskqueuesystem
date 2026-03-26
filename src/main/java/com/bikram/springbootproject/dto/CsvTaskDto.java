package com.bikram.springbootproject.dto;

import lombok.Data;
@Data
public class CsvTaskDto {
    private long rowNumber;
    private String taskName;
    private String type;
    private String payload;
    private String executionTime;
    private String result;
    private String error;
}
