package com.lib.exceptionhandler;

import java.time.LocalDateTime;
import java.util.List;

public class StandardErrorResponse {
    private String code;
    private String message;
    private LocalDateTime timestamp;
    private List<String> details;

    // Constructor por defecto necesario para la deserialización
    public StandardErrorResponse() {
    }

    public StandardErrorResponse(String code, String message, LocalDateTime timestamp, List<String> details) {
        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
        this.details = details;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }
}
