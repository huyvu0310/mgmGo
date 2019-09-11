package com.mgmtp.internship.experiences.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiResponse {

    public enum ApiResponseStatus {SUCCESS, FAILED}

    @JsonProperty("status")
    private final ApiResponseStatus responseStatus;

    @JsonProperty("message")
    private final String message;

    ApiResponse(final ApiResponseStatus responseStatus, final String message) {
        this.responseStatus = responseStatus;
        this.message = message;
    }

    public static ApiResponse success(final String message) {
        return new ApiResponse(ApiResponseStatus.SUCCESS, message);
    }

    public static ApiResponse failed(final String message) {
        return new ApiResponse(ApiResponseStatus.FAILED, message);
    }

    public ApiResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public String getMessage() {
        return message;
    }
}
