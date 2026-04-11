package com.bikram.springbootproject.service;


import com.bikram.springbootproject.dto.CsvProcessPayload;
import com.bikram.springbootproject.model.Task;
import com.bikram.springbootproject.model.TaskType;
import com.bikram.springbootproject.repo.TaskRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class CsvProcessTaskHandler {

    private final ObjectMapper objectMapper;
    private final TaskRepo taskRepo;

    public String handle(Task task) throws Exception {
        CsvProcessPayload csvProcessPayload = objectMapper.readValue(task.getPayload(), CsvProcessPayload.class);
        log.info("CSV_PROCESS [{}] url = {} op = {}",task.getId(),csvProcessPayload.getFileUrl(),csvProcessPayload.getOperation());
        List<CSVRecord> records = fetchAndParse(csvProcessPayload.getFileUrl());
        List<Task> validTask = new ArrayList<>();
        for (CSVRecord record : records){
            try {
                validTask.add(rowToTask(record));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
return String.format("CSV_%s — total=%d imported=%d failed=%d",csvProcessPayload.getOperation().toUpperCase(),records.size(),validTask.size());
    }

    private List<CSVRecord> fetchAndParse(String fileUrl) throws Exception{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL(fileUrl).openStream()));
        CSVParser parser = CSVFormat.DEFAULT
                .builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setTrim(true)
                .build()
                .parse(bufferedReader);

        return parser.getRecords();

    }

    private Task rowToTask(CSVRecord record){
        String taskName = record.get("taskName");
        String type = record.get("type").toUpperCase();
        if(type.equals("CSV_IMPORT")) type = "CSV_PROCESS";
        Task t = new Task();
        t.setTaskName(taskName);
        t.setType(TaskType.valueOf(type));
        t.setPayload(safeGet(record,"csvProcessPayload"));

        return t;
    }

    private String safeGet(CSVRecord record, String col) {
        try { return record.get(col); } catch (Exception e) { return null; }
    }

}
