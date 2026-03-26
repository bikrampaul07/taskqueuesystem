package com.bikram.springbootproject.validator;

import com.bikram.springbootproject.dto.CsvTaskDto;
import com.bikram.springbootproject.dto.CsvUploadResponse.CsvRowError;
import com.bikram.springbootproject.model.TaskType;

import java.util.ArrayList;
import java.util.List;

public class CsvTaskValidator {

    public static List<CsvRowError> validate(CsvTaskDto dto) {
        List<CsvRowError> errors = new ArrayList<>();
        long row = dto.getRowNumber();

        // taskName - required, max 255
        if (dto.getTaskName() == null || dto.getTaskName().isBlank()) {
            errors.add(new CsvRowError(row, "taskName", "taskName is required"));
        } else if (dto.getTaskName().length() > 255) {
            errors.add(new CsvRowError(row, "taskName", "taskName must not exceed 255 characters"));
        }

        // type - required, must be valid enum value
        if (dto.getType() == null || dto.getType().isBlank()) {
            errors.add(new CsvRowError(row, "type", "type is required"));
        } else {
            try {
                TaskType.valueOf(dto.getType().toUpperCase());
            } catch (IllegalArgumentException e) {
                errors.add(new CsvRowError(row, "type",
                        "Invalid type '" + dto.getType() + "'. Valid values: " + validTaskTypes()));
            }
        }

        // executionTime - optional, but must be a valid long if provided
        if (dto.getExecutionTime() != null && !dto.getExecutionTime().isBlank()) {
            try {
                long val = Long.parseLong(dto.getExecutionTime());
                if (val < 0) {
                    errors.add(new CsvRowError(row, "executionTime", "executionTime must be a non-negative number"));
                }
            } catch (NumberFormatException e) {
                errors.add(new CsvRowError(row, "executionTime", "executionTime must be a valid number"));
            }
        }

        return errors;
    }

    private static String validTaskTypes() {
        StringBuilder sb = new StringBuilder();
        TaskType[] values = TaskType.values();
        for (int i = 0; i < values.length; i++) {
            sb.append(values[i].name());
            if (i < values.length - 1) sb.append(", ");
        }
        return sb.toString();
    }
}
