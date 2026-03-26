package com.bikram.springbootproject.util;

import com.bikram.springbootproject.dto.CsvTaskDto;
import com.bikram.springbootproject.model.TaskStatus;
import com.bikram.springbootproject.model.TaskType;
import org.apache.commons.csv.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

public class CsvHelper {

    public static final String TYPE = "text/csv";

    public static boolean hasCsvFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType())
                || (file.getOriginalFilename() != null
                && file.getOriginalFilename().endsWith(".csv"));
    }

    public static List<CsvTaskDto> parseCsv(InputStream is) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is));
             CSVParser csvParser = new CSVParser(reader,
                     CSVFormat.DEFAULT
                             .withFirstRecordAsHeader()
                             .withIgnoreHeaderCase()
                             .withTrim()
                             .withQuote('"')
                             .withEscape('\\')
                             .withIgnoreEmptyLines(true)
                             .withAllowMissingColumnNames(true))) {

            List<CsvTaskDto> tasks = new ArrayList<>();

            for (CSVRecord record : csvParser) {
                CsvTaskDto dto = new CsvTaskDto();
                dto.setRowNumber(record.getRecordNumber());
                dto.setTaskName(record.get("taskName"));
                dto.setType(record.get("type"));
                dto.setPayload(record.isMapped("payload") ? record.get("payload") : null);
                dto.setExecutionTime(record.isMapped("executionTime") ? record.get("executionTime") : null);
                dto.setResult(record.isMapped("result") ? record.get("result") : null);
                dto.setError(record.isMapped("error") ? record.get("error") : null);
                tasks.add(dto);
            }

            return tasks;

        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage(), e);
        }
    }

    public static ByteArrayInputStream tasksToCSV(List<com.bikram.springbootproject.model.Task> tasks) {
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL)
                .withHeader("id", "taskName", "type", "payload", "status",
                        "executionTime", "result", "error", "createdAt", "updatedAt");
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter printer = new CSVPrinter(new PrintWriter(out), format)) {

            for (var task : tasks) {
                printer.printRecord(
                        task.getId(),
                        task.getTaskName(),
                        task.getType(),
                        task.getPayload(),
                        task.getStatus(),
                        task.getExecutionTime(),
                        task.getResult(),
                        task.getError(),
                        task.getCreatedAt(),
                        task.getUpdatedAt()
                );
            }
            printer.flush();
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Failed to export tasks to CSV: " + e.getMessage(), e);
        }
    }
}
