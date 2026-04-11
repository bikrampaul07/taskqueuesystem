package com.bikram.springbootproject.dto;

import com.bikram.springbootproject.model.TaskType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class TaskSubmitRequest {

    @NotBlank(message = "taskname can not be empty")
    private String taskName;

    @NotNull(message = "TaskType can not be null")
    private TaskType taskType;

    private Object payload;

    @Min(0) @Max(10)
    private int priority = 0;

    @Min(0) @Max(5)
    private int maxRetries;
}
