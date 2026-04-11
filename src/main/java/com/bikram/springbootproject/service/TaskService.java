package com.bikram.springbootproject.service;

import com.bikram.springbootproject.dto.*;
import com.bikram.springbootproject.dto.TaskStatusResponse;
import com.bikram.springbootproject.model.Task;
import com.bikram.springbootproject.model.TaskStatus;
import com.bikram.springbootproject.model.TaskType;
import com.bikram.springbootproject.repo.TaskRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepo repo;
    private final ObjectMapper objectMapper;
    private final TaskProducer taskProducer;

    // =========================================================================
    // Phase 1 – Task Submission API (non-blocking)
    // =========================================================================

    /**
     * Accepts task type + payload, saves as PENDING, returns UUID immediately.
     * The background TaskWorker picks it up asynchronously.
     */
    @Transactional
    public TaskSubmitResponse submitTask(TaskSubmitRequest request) throws Exception {
        Task task = new Task();
        task.setTaskName(request.getTaskName());
        task.setType(request.getTaskType());
        task.setPayload(objectMapper.writeValueAsString(request.getPayload()));
        task.setPriority(request.getPriority());      // Phase 2A
        task.setMaxRetries(request.getMaxRetries());  // Phase 2B
        repo.save(task);
        log.info("Task submitted — id={} type={} priority={} name={}",
                task.getId(), task.getType(), task.getPriority(), task.getTaskName());


        return new TaskSubmitResponse(
                task.getId(),
                task.getTaskName(),
                task.getType(),
                task.getStatus(),
                task.getPriority(),
                task.getCreatedAt(),
                "Task accepted. Poll GET /tasks/" + task.getId() + "/status for updates."
        );
    }


    /** Full status of a single task by UUID. */
    public TaskStatusResponse getTaskStatus(UUID id) {
        return TaskStatusResponse.from(findOrThrow(id));
    }

    /** Returns raw Task entity (used internally). */
    public Task findTask(String id) {
        return findOrThrow(UUID.fromString(id));
    }

    /** All tasks, no filter. */
    public List<TaskStatusResponse> getAllTasks() {
        List<Task> tasks = repo.findAll();
        List<TaskStatusResponse> responses = new ArrayList<>();
        for (Task task : tasks){
            responses.add(TaskStatusResponse.from(task));
        }
        return responses;
    }

    /** Filter by status only. */
    public List<TaskStatusResponse> filterByStatus(TaskStatus status) {
        List<Task> tasks =  repo.findByStatus(status);
        List<TaskStatusResponse> responses = new ArrayList<>();
        for (Task task :tasks){
            responses.add(TaskStatusResponse.from(task));
        }
        return responses;
    }

    // Filter by status + type //
    public List<TaskStatusResponse> filterByStatusAndType(TaskStatus status, TaskType type) {
        return repo.findByStatusAndType(status, type).stream()
                .map(TaskStatusResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public UUID createTask(String taskName, Object payload) throws Exception {
        Task task = new Task();
        task.setTaskName(taskName);
        task.setType(TaskType.OTHERS);
        task.setPayload(objectMapper.writeValueAsString(payload));
        task.setStatus(TaskStatus.PENDING);
        repo.save(task);

        // Publish to RabbitMQ
        taskProducer.publishTask(task);

        log.info("Legacy task created & published — id={} name={}", task.getId(), taskName);
        return task.getId();
    }

    /** Manual status override (admin / testing). */
    @Transactional
    public TaskStatusResponse updateStatus(UUID taskId, TaskStatus status) {
        Task task = findOrThrow(taskId);
        task.setStatus(status);
        repo.save(task);
        log.info("Task [{}] status manually set → {}", taskId, status);
        return TaskStatusResponse.from(task);
    }

    private Task findOrThrow(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found: " + id));
    }
}
