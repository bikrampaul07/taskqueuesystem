package com.bikram.springbootproject.service;


import com.bikram.springbootproject.dto.EmailPayload;
import com.bikram.springbootproject.model.Task;
import com.bikram.springbootproject.model.TaskStatus;
import com.bikram.springbootproject.model.TaskType;
import com.bikram.springbootproject.repo.TaskRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class TaskService {
    @Autowired
    private TaskRepo repo;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskExecutorService executorService;

    public UUID createTask(String taskName, Object payload) throws Exception{
        Task task=new Task();
        task.setTaskName(taskName);
        task.setType(TaskType.OTHERS);
        String json = objectMapper.writeValueAsString(payload);
        task.setPayload(json);
        repo.save(task);
        UUID taskId=task.getId();
        log.info("id={} and taskName ={}",task.getId(),task.getTaskName());
        return taskId;
    }

    public Task findTask(String id){
        return repo.findById(UUID.fromString(id)).orElseThrow(()->new RuntimeException("task not found"));
    }

    public List<Task> getAllTasks(){
        return repo.findAll();
    }

    public List<Task> filterTask(TaskStatus status,TaskType type){
        return repo.findByStatusAndType(status,type);
    }

    public List<Task> filterTaskByStatus(TaskStatus status){
        return repo.findByStatus(status);
    }

    public Task updateTask(UUID taskId,TaskStatus status){
       Task task= repo.findById(taskId).orElseThrow(()->new RuntimeException("task not found"));
        task.setStatus(status);
        repo.save(task);
        return task;
    }

}
