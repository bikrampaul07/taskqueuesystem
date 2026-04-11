package com.bikram.springbootproject.dto;


import lombok.Data;

@Data
public class ReportGenerationPayload {
    private String reportTitle = "Task Report";
    private String format = "pdf";
}
