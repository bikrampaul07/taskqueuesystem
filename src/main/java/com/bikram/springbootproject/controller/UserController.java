package com.bikram.springbootproject.controller;


import com.bikram.springbootproject.model.User;
import com.bikram.springbootproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public Long createUser(@RequestBody User user){
        userService.createUser(user);
        return user.getId();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long userId){
        return userService.getUser(userId);
    }

    @GetMapping
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }


    @PostMapping("/assign/{taskId}")
    public String assignTask(@PathVariable UUID taskId,@RequestBody List<Long> userIds){
        userService.assignTask(taskId,userIds);
        return "Task assigned successfully";
    }
}
