package com.bikram.springbootproject.worker;

import com.bikram.springbootproject.dto.EmailPayload;
import com.bikram.springbootproject.model.Task;
import com.bikram.springbootproject.model.TaskStatus;
import com.bikram.springbootproject.repo.TaskRepo;
import com.bikram.springbootproject.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskWorker {
    @Autowired
    private TaskRepo taskRepository;
    @Autowired
    private EmailService emailService;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelayString = "${task.worker.poll-interval-ms:10000}")
    public void processPendingTasks() {
        List<Task> pendingTasks = taskRepository.findByStatus(TaskStatus.PENDING);

        if (pendingTasks.isEmpty()) {
            log.debug("No pending tasks found.");
            return;
        }
        log.info("Worker picked up {} pending task(s)", pendingTasks.size());
        for (Task task : pendingTasks) {
            try {
                // Mark as RUNNING so other worker instances don't pick it up
                task.setStatus(TaskStatus.RUNNING);
                taskRepository.save(task);
                dispatch(task);

            } catch (Exception e) {
                log.error("Unexpected error dispatching task [{}]: {}", task.getId(), e.getMessage());
                markFailed(task, e.getMessage());
            }
        }
    }

    private void dispatch(Task task) {
        log.info("Processing task [{}] of type [{}]", task.getId(), task.getType());

        switch (task.getType()) {
            case EMAIL_SEND -> handleEmailTask(task);
            case CSV_IMPORT -> handleCsvImportTask(task);
            case OTHERS     -> handleOtherTask(task);
            default         -> markFailed(task, "Unknown task type: " + task.getType());
        }
    }

  //All Task Handlers
    private void handleEmailTask(Task task) {
        long start = System.currentTimeMillis();
        try {
            EmailPayload payload = objectMapper.readValue(task.getPayload(), EmailPayload.class);
            emailService.mailSend(payload);

            markCompleted(task, "Email sent to " + payload.getTo(), System.currentTimeMillis() - start);

        } catch (Exception e) {
            markFailed(task, "EMAIL_SEND failed: " + e.getMessage());
            log.error("Email task [{}] failed: {}", task.getId(), e.getMessage());
        }
    }

    private void handleCsvImportTask(Task task) {
        long start = System.currentTimeMillis();
        try {
            // We can also add our CSV_Import logic here
            log.info("CSV_IMPORT task [{}] — payload: {}", task.getId(), task.getPayload());

            markCompleted(task, "CSV import processed", System.currentTimeMillis() - start);

        } catch (Exception e) {
            markFailed(task, "CSV_IMPORT failed: " + e.getMessage());
            log.error("CSV import task [{}] failed: {}", task.getId(), e.getMessage());
        }
    }

    private void handleOtherTask(Task task) {
        long start = System.currentTimeMillis();
        try {
            log.info("OTHERS task [{}] — payload: {}", task.getId(), task.getPayload());

            markCompleted(task, "Task processed", System.currentTimeMillis() - start);

        } catch (Exception e) {
            markFailed(task, "OTHERS task failed: " + e.getMessage());
        }
    }

    //Status handlers
    private void markCompleted(Task task, String result, long executionTime) {
        task.setStatus(TaskStatus.COMPLETED);
        task.setResult(result);
        task.setExecutionTime(executionTime);
        task.setError(null);
        taskRepository.save(task);
        log.info("Task [{}] COMPLETED in {}ms — {}", task.getId(), executionTime, result);
    }

    private void markFailed(Task task, String error) {
        task.setStatus(TaskStatus.FAILED);
        task.setError(error);
        taskRepository.save(task);
        log.error("Task [{}] FAILED — {}", task.getId(), error);
    }

}
