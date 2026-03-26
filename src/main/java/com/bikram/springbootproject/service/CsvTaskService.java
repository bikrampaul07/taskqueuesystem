package com.bikram.springbootproject.service;

import com.bikram.springbootproject.dto.CsvTaskDto;
import com.bikram.springbootproject.dto.CsvUploadResponse;
import com.bikram.springbootproject.dto.CsvUploadResponse.CsvRowError;
import com.bikram.springbootproject.model.Task;
import com.bikram.springbootproject.model.TaskType;
import com.bikram.springbootproject.repo.TaskRepo;
import com.bikram.springbootproject.util.CsvHelper;
import com.bikram.springbootproject.validator.CsvTaskValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CsvTaskService {

    private final TaskRepo taskRepository;

    /**
     * Uploads, validates, and saves tasks from a CSV file.
     */
    public CsvUploadResponse uploadTasks(MultipartFile file) {
        if (!CsvHelper.hasCsvFormat(file)) {
            throw new IllegalArgumentException("Invalid file format. Please upload a valid CSV file.");
        }

        List<CsvTaskDto> csvRows;
        try {
            csvRows = CsvHelper.parseCsv(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read the uploaded file.", e);
        }

        List<CsvRowError> allErrors = new ArrayList<>();
        List<Task> validTasks = new ArrayList<>();

        for (CsvTaskDto dto : csvRows) {
            List<CsvRowError> rowErrors = CsvTaskValidator.validate(dto);

            if (!rowErrors.isEmpty()) {
                allErrors.addAll(rowErrors);
                log.warn("Row {} has {} validation error(s)", dto.getRowNumber(), rowErrors.size());
            } else {
                validTasks.add(mapToTask(dto));
            }
        }

        if (!validTasks.isEmpty()) {
            taskRepository.saveAll(validTasks);
            log.info("Saved {} tasks from CSV upload", validTasks.size());
        }

        int failedCount = (int) allErrors.stream()
                .map(CsvRowError::getRowNumber)
                .distinct()
                .count();

        CsvUploadResponse response = new CsvUploadResponse();
        response.setTotalRows(csvRows.size());
        response.setSavedCount(validTasks.size());
        response.setFailedCount(failedCount);
        response.setErrors(allErrors);
        return response;
    }

    /**
     * Exports all tasks from DB to a CSV file as a stream.
     */
    public ByteArrayInputStream exportTasksToCsv() {
        List<Task> tasks = taskRepository.findAll();
        return CsvHelper.tasksToCSV(tasks);
    }

    // -------------------------------------------------------------------------
    // Private helper
    // -------------------------------------------------------------------------

    private Task mapToTask(CsvTaskDto dto) {
        Task task = new Task();
        task.setTaskName(dto.getTaskName());
        task.setType(TaskType.valueOf(dto.getType().toUpperCase()));
        task.setPayload(dto.getPayload());
        task.setResult(dto.getResult());
        task.setError(dto.getError());

        if (dto.getExecutionTime() != null && !dto.getExecutionTime().isBlank()) {
            task.setExecutionTime(Long.parseLong(dto.getExecutionTime()));
        }

        return task;
    }
}
