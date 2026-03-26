package com.bikram.springbootproject.dto;

import com.bikram.springbootproject.model.TaskType;
import lombok.Data;

@Data
public class TaskReq {
    public String taskName;
    public Object payload;
}
