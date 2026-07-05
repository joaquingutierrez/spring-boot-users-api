package com.joaquingutierrez.users_api.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.joaquingutierrez.users_api.enums.ErrorCode;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private ErrorCode code;
    private String message;
    private Map<String, List<String>> errors = new HashMap<>();

    public ErrorResponse() {
    }

    public ErrorResponse(LocalDateTime timestamp, int status, ErrorCode code, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public ErrorResponse(LocalDateTime timestamp, int status, ErrorCode code, String message, Map<String, List<String>> errors) {
        this(timestamp, status, code, message);
        this.errors = errors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ErrorCode getCode() {
        return code;
    }

    public void setCode(ErrorCode code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, List<String>> errors) {
        this.errors = errors;
    }
}
