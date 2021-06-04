package com.agh.EventarzGateway.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDTO {
    private int status;
    private String error;
    private String path;
    private String timestamp;
    private String message;
}
