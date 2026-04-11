package com.bikram.springbootproject.dto;


import lombok.Data;

@Data
public class CsvProcessPayload {

    private String fileUrl;
    private String operation = "IMPORT";
}
