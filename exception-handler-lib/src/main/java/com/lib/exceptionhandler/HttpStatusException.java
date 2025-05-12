package com.lib.exceptionhandler;

public class HttpStatusException extends RuntimeException {
    private final String httpStatusCode;

    public HttpStatusException(String message, String httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }

    public String getHttpStatusCode() {
        return httpStatusCode;
    }
}
