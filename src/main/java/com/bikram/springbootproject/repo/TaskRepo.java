package com.bikram.springbootproject.repo;

import com.bikram.springbootproject.model.Task;
import com.bikram.springbootproject.model.TaskStatus;
import com.bikram.springbootproject.model.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepo extends JpaRepository<Task, UUID> {

    List<Task> findByStatusAndType(TaskStatus status, TaskType type);

    List<Task> findByStatus(TaskStatus status);

@Query("""
    SELECT t FROM Task t
            WHERE t.status = 'PENDING'
            ORDER BY t.priority DESC, t.createdAt ASC
""")
    List <Task> findPendingOrderedByPriority();

@Query("""
 SELECT t FROM Task t
            WHERE t.status = 'RETRYING'
              AND t.nextRetryAt <= :now
            ORDER BY t.priority DESC, t.nextRetryAt ASC
""")
    List<Task> findRetryableTasks(@Param("now")LocalDateTime now);

@Query("""
            SELECT t FROM Task t
            WHERE (t.status = 'PENDING')
               OR (t.status = 'RETRYING' AND t.nextRetryAt <= :now)
            ORDER BY t.priority DESC, t.createdAt ASC
""")
    List<Task> findWorkableTasks(@Param("now") LocalDateTime now);



}
