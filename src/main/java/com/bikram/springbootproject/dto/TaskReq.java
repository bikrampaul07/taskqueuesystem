package com.bikram.springbootproject.dto;

import com.bikram.springbootproject.model.TaskType;
import lombok.Data;

@Data
public class TaskReq {
    public TaskType type;
    public Object payload;

}
