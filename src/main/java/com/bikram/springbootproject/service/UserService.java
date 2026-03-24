package com.bikram.springbootproject.service;


import com.bikram.springbootproject.model.Task;
import com.bikram.springbootproject.model.User;
import com.bikram.springbootproject.repo.TaskRepo;
import com.bikram.springbootproject.repo.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TaskRepo taskRepo;

    public Long createUser(User user){
        if(user.getEmail() == null || user.getEmail().isEmpty()){
            throw new RuntimeException("mail required");
        }
        if(userRepo.existsByEmail(user.getEmail())){
            throw new RuntimeException("Email is already registered");
        }
        userRepo.save(user);
        return user.getId();
    }

    public User getUser(Long id){
        return userRepo.findById(id).orElseThrow(()->new RuntimeException("user not found"));
    }

    public List<User> getAllUsers(){
        return userRepo.findAll();
    }

    public void assignTask(UUID taskId, List<Long> userIds){
        Task task = taskRepo.findById(taskId).orElseThrow(()->new RuntimeException("task not found"));
        List<User> users=userRepo.findAllById(userIds);
        if(users.isEmpty()){
            throw new RuntimeException("user not found");
        }

        task.setUsers(users);
        taskRepo.save(task);

        log.info("task_id={} assigned users = {}",taskId,userIds);

    }

    public List<User> getUserByTaskId(UUID taskId){
        return userRepo.findByTasks_Id(taskId);
    }

}
