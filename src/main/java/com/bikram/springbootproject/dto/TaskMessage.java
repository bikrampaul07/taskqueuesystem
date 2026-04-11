package com.bikram.springbootproject.dto;

import com.bikram.springbootproject.model.TaskType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskMessage implements Serializable {

    private UUID taskId;
    private TaskType taskType;
    private int priority;
    private int retryCount;
}
