package com.m3.skinnyrest;

public class RestException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final int _statuscode;

    public RestException() {
        super();
        _statuscode = 500;
    }

    public RestException(String message) {
        super(message);
        _statuscode = 500;
    }

    public RestException(String message, int statuscd) {
        super(message);
        _statuscode = statuscd;
    }

    public RestException(Throwable cause) {
        super(cause);
        _statuscode = 500;
    }

    public RestException(Throwable cause, int statuscd) {
        super(cause);
        _statuscode = statuscd;
    }

    public RestException(String message, Throwable cause) {
        super(message, cause);
        _statuscode = 500;
    }

    public RestException(String message, Throwable cause, int statuscd) {
        super(message, cause);
        _statuscode = statuscd;
    }

    public int getStatusCode() { return _statuscode; }
}
