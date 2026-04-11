package com.bikram.springbootproject.service;

import com.bikram.springbootproject.dto.ReportGenerationPayload;
import com.bikram.springbootproject.model.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@RequiredArgsConstructor
@Data
@Component
@Slf4j
public class ReportGenerationService {
    private final ObjectMapper objectMapper;
    private final ReportService reportService;

    public  String handle(Task task) throws Exception{
        ReportGenerationPayload reportGenerationPayload = null;
        if(task.getPayload() != null && !task.getPayload().isBlank() && !task.getPayload().equals("null")){
            try {
                reportGenerationPayload = objectMapper.readValue(task.getPayload(), ReportGenerationPayload.class);
            }catch (Exception e){
                log.warn("Could not parse report payload -- using defaults. Reason: {}", e.getMessage());
            }
        }
        String title  = (reportGenerationPayload != null && reportGenerationPayload.getReportTitle() != null) ? reportGenerationPayload.getReportTitle() : "Task Report";
        String format = (reportGenerationPayload != null && reportGenerationPayload.getFormat() != null) ? reportGenerationPayload.getFormat().toUpperCase() : "PDF";

        log.info("REPORT_GENERATION task [{}] — title='{}' format={}", task.getId(), title, format);

        ByteArrayInputStream pdfStream = reportService.generatePdfReport();
        int sizeBytes = pdfStream.available();

        log.info("PDF report '{}' generated — {}KB", title, sizeBytes / 1024);
        return String.format("PDF report '%s' generated — %dKB", title, sizeBytes / 1024);
    }
}
