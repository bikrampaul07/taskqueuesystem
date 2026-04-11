//package com.bikram.springbootproject.service;
//
//import com.bikram.springbootproject.dto.EmailPayload;
//import com.bikram.springbootproject.model.Task;
//import com.bikram.springbootproject.model.TaskStatus;
//import com.bikram.springbootproject.model.TaskType;
//import com.bikram.springbootproject.repo.TaskRepo;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//import java.util.concurrent.ThreadLocalRandom;
//
//@Slf4j
//@Service
//public class TaskExecutorService {
//    @Autowired
//    private TaskRepo repo;
//    @Autowired
//    private EmailService emailService;
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Async
//    public void execute(UUID taskId){
//        Task task=repo.findById(taskId).orElseThrow(()->new RuntimeException("Task not found"));
//        long start=System.currentTimeMillis();
//        try {
//            task.setStatus(TaskStatus.RUNNING);
//            repo.save(task);
//            if(task.getType() == TaskType.EMAIL_SEND){
//                handleMail(task);
//            }
//            task.setStatus(TaskStatus.COMPLETED);
//            task.setResult("Success");
//        } catch (Exception e) {
//            log.info("task failed id = {}",task.getId(),e);
//            task.setStatus(TaskStatus.FAILED);
//            task.setResult(e.getMessage());
//        }
//        finally {
//            long end = System.currentTimeMillis();
//            task.setExecutionTime(end-start);
//            task.setUpdatedAt(LocalDateTime.now());
//            repo.save(task);
//        }
//    }
//    private void handleMail(Task task) throws InterruptedException, JsonProcessingException {
//        EmailPayload load = objectMapper.readValue(task.getPayload(), EmailPayload.class);
//        emailService.mailSend(load);
//        int delay = ThreadLocalRandom.current().nextInt(2000,5000);
//        Thread.sleep(delay);
//    }
//
//
//}
