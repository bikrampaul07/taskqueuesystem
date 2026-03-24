package com.bikram.springbootproject.repo;

import com.bikram.springbootproject.model.Task;
import com.bikram.springbootproject.model.TaskStatus;
import com.bikram.springbootproject.model.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepo extends JpaRepository<Task, UUID> {

    List<Task> findByStatusAndType(TaskStatus status, TaskType type);

    List<Task> findByStatus(TaskStatus status);
}
