package com.mgmtp.internship.experiences.exceptions;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

    private final HttpStatus responseStatus;

    public ApiException(final HttpStatus responseStatus, final String msg, final Throwable t) {
        super(msg, t);
        this.responseStatus = responseStatus;
    }

    public ApiException(final HttpStatus responseStatus, final String msg) {
        super(msg);
        this.responseStatus = responseStatus;
    }

    public HttpStatus getResponseStatus() {
        return responseStatus;
    }
}
