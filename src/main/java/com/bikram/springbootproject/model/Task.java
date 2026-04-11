package com.bikram.springbootproject.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "task", indexes = {
        // Speed up the worker's queue poll queries
        @Index(name = "idx_task_status", columnList = "status"),
        @Index(name = "idx_task_priority_created", columnList = "priority, createdAt")
})
public class Task {
    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR")
    private TaskType type;

    @Column(nullable = false)
    private String taskName;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private int priority;

    @Column(nullable = false)
    private int retryCount = 0;

    @Column(nullable = false)
    private int maxRetries = 5;

    private LocalDateTime nextRetryAt;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private Long executionTime;
    private  String result;
    private String error;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToMany
    @JoinTable(
            name = "task_user" ,
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users;

    @PrePersist
    public  void PrePersist(){
        this.createdAt=LocalDateTime.now();
        this.updatedAt=LocalDateTime.now();
        this.status= TaskStatus.PENDING;
    }

    @PreUpdate
    public void PreUpdate(){
        this.updatedAt=LocalDateTime.now();
    }

}
