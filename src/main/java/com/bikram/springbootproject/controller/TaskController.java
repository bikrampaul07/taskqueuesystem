package com.bikram.springbootproject.controller;

import com.bikram.springbootproject.dto.EmailPayload;
import com.bikram.springbootproject.dto.TaskReq;
import com.bikram.springbootproject.model.Task;
import com.bikram.springbootproject.model.TaskStatus;
import com.bikram.springbootproject.model.TaskType;
import com.bikram.springbootproject.service.EmailService;
import com.bikram.springbootproject.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService service;

    @Autowired
    private EmailService emailService;


    @PostMapping()
    public UUID createTask(@RequestBody TaskReq req) throws Exception {
      return service.createTask(req.getTaskName(),req.getPayload());
 }

    @GetMapping("/{id}")
    public Task findTask(@PathVariable String id){
        return service.findTask(id);
 }

    @GetMapping()
    public List<Task> filterTaskByStatus(@RequestParam TaskStatus status){
        return service.filterTaskByStatus(status);
    }

    @GetMapping("/filter")
    public List<Task> filterTask(@RequestParam TaskStatus status, @RequestParam TaskType type){
        return  service.filterTask(status,type);
    }

    @PostMapping("/email")
    public  String sendMail(@RequestBody EmailPayload payload) throws Exception {
        if (payload.getTo() == null || payload.getTo().isEmpty()) {
            throw new RuntimeException("Recipient email is required");
        }
        emailService.mailSend(payload);
        return "SUCCESS";
    }

    @PostMapping("/status/update")
    public Task updateStatus(@RequestParam UUID taskId,@RequestBody TaskStatus status){
       return service.updateTask(taskId,status);
    }
}
