package com.agh.EventarzGateway.model.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ErrorDTO {
    private int status;
    private String error;
    private String path;
    private String timestamp;
    private String message;

    public ErrorDTO(int status, String error, String path, String message) {
        this.status = status;
        this.error = error;
        this.path = path;
        this.timestamp = LocalDateTime.now().toString();
        this.message = message;
    }
}
