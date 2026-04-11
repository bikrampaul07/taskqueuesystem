package com.bikram.springbootproject.dto;

import com.bikram.springbootproject.model.Task;
import com.bikram.springbootproject.model.TaskStatus;
import com.bikram.springbootproject.model.TaskType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Returned from GET /tasks/{id}/status and GET /tasks listing endpoints.
 */
@Data
public class TaskStatusResponse {

    private UUID id;
    private String taskName;
    private TaskType type;
    private TaskStatus status;
    private int priority;

    // Result fields
    private String result;
    private String error;
    private Long executionTimeMs;

    // Phase 2B – retry fields
    private int retryCount;
    private int maxRetries;
    private LocalDateTime nextRetryAt;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TaskStatusResponse from(Task task) {
        TaskStatusResponse r = new TaskStatusResponse();
        r.setId(task.getId());
        r.setTaskName(task.getTaskName());
        r.setType(task.getType());
        r.setStatus(task.getStatus());
        r.setPriority(task.getPriority());
        r.setResult(task.getResult());
        r.setError(task.getError());
        r.setExecutionTimeMs(task.getExecutionTime());
        r.setRetryCount(task.getRetryCount());
        r.setMaxRetries(task.getMaxRetries());
        r.setNextRetryAt(task.getNextRetryAt());
        r.setCreatedAt(task.getCreatedAt());
        r.setUpdatedAt(task.getUpdatedAt());
        return r;
    }
}
