package com.bikram.springbootproject.dto;


import com.bikram.springbootproject.model.TaskStatus;
import com.bikram.springbootproject.model.TaskType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskSubmitResponse {

    private UUID taskId;
    private String taskName;
    private TaskType taskType;
    private TaskStatus taskStatus;
    private int priority;
    private LocalDateTime createdAt;
    private String message;

}
