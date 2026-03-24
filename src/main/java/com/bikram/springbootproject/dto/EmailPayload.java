package com.bikram.springbootproject.dto;


import lombok.Data;

@Data
public class EmailPayload {
    public String to;
    public String subject;
    public String body;
}
