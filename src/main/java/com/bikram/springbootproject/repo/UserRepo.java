package com.bikram.springbootproject.repo;

import com.bikram.springbootproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserRepo extends JpaRepository<User,Long> {

    List<User> findByTasks_Id(UUID taskId);

    boolean existsByEmail(String email);
}
