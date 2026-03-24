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

    public UUID createTask(TaskType type, Object payload) throws Exception{
        Task task=new Task();
        task.setType(type);
        String json = objectMapper.writeValueAsString(payload);
        task.setPayload(json);
        repo.save(task);
        UUID taskId=task.getId();
        if(type == TaskType.EMAIL_SEND){
            executorService.execute(taskId);
        }
        log.info("id={} and type ={}",task.getId(),type);
        return task.getId();
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



}
