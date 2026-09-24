package com.cambistaonline.auth.shared;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private final boolean success = false;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final List<String> validationErrors;
    private final LocalDateTime timestamp;

    public ErrorResponse(int status, String error, String message, String path, List<String> validationErrors) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.validationErrors = validationErrors;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String error, String message, String path) {
        this(status, error, message, path, null);
    }

    public boolean isSuccess() { return success; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public List<String> getValidationErrors() { return validationErrors; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
