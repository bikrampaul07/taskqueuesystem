package com.bikram.springbootproject.controller;

import com.bikram.springbootproject.dto.CsvUploadResponse;
import com.bikram.springbootproject.service.CsvTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api/tasks/csv")
@RequiredArgsConstructor
public class CsvTaskController {

    private final CsvTaskService csvTaskService;

    /**
     * POST /api/tasks/csv/upload
     * Accepts a multipart CSV file, validates each row, and saves valid tasks to PostgreSQL.
     *
     * Form field name: "file"
     * Expected CSV headers: taskName, type, payload (opt), executionTime (opt), result (opt), error (opt)
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CsvUploadResponse> uploadCsv(@RequestParam("file") MultipartFile file) {
        CsvUploadResponse response = csvTaskService.uploadTasks(file);

        HttpStatus status = response.getFailedCount() == 0 ? HttpStatus.OK : HttpStatus.MULTI_STATUS;
        return ResponseEntity.status(status).body(response);
    }

    /**
     * GET /api/tasks/csv/export
     * Exports all tasks from the DB as a downloadable CSV file.
     */
    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportCsv() {
        ByteArrayInputStream stream = csvTaskService.exportTasksToCsv();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tasks.csv");
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(stream));
    }
}
