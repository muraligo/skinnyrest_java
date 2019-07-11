package com.m3.skinnyrest.rest;

public enum RestResponseCode {
    /* success codes 2xx */
    OK(200, "Success"), 
    CREATED(201, "Created"),
    ACCEPTED(202, "Accepted"),
    NO_CONTENT(204, "No Content"),
    RESET_CONTENT(205, "Reset Content"),
    PARTIAL_CONTENT(206, "Reset Content"),
    /* informational */
    MOVED_PERMANENTLY(301, "Moved Permanently"),
    FOUND(302, "Found"),
    SEE_OTHER(303, "See Other"),
    NOT_MODIFIED(304, "Not Modified"),
    USE_PROXY(305, "Use Proxy"),
    TEMPORARY_REDIRECT(307, "Temporary Redirect"),
    /* client error */
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    PAYMENT_REQUIRED(402, "Payment Required"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    NOT_ACCEPTABLE(406, "Not Acceptable"),
    PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
    REQUEST_TIMEOUT(408, "Request Timeout"),
    CONFLICT(409, "Conflict"),
    GONE(410, "Gone"),
    LENGTH_REQUIRED(411, "Length Required"),
    PRECONDITION_FAILED(412, "Precondition Failed"),
    REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),
    REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
    REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"),
    EXPECTATION_FAILED(417, "Expectation Failed"),
    PRECONDITION_REQUIRED(428, "Precondition required"),
    TOO_MANY_REQUESTS(429, "Too Many Requests"),
    REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large"),
    /* server error */
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not Implemented"),
    BAD_GATEWAY(502, "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    GATEWAY_TIMEOUT(504, "Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),
    NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required")
    ;

    private final int _code;
    private final String _basemessage;

    private RestResponseCode(int cd, String msg) {
        _code = cd;
        _basemessage = msg;
    }

    public int code() { return _code; }
    public String baseMessage() { return _basemessage; }

    public static RestResponseCode forCode(int targetcode) {
        for (RestResponseCode cd : values()) {
            if (cd._code == targetcode) return cd;
        }
        return null;
    }
    /*
    Enum Constant and Description
    OK
    200 OK, see HTTP/1.1 documentation.
    CREATED
    201 Created, see HTTP/1.1 documentation.
    ACCEPTED
    202 Accepted, see HTTP/1.1 documentation.
    NO_CONTENT
    204 No Content, see HTTP/1.1 documentation.
    RESET_CONTENT
    205 Reset Content, see HTTP/1.1 documentation.
    PARTIAL_CONTENT
    206 Reset Content, see HTTP/1.1 documentation.
    MOVED_PERMANENTLY
    301 Moved Permanently, see HTTP/1.1 documentation.
    FOUND
    302 Found, see HTTP/1.1 documentation.
    SEE_OTHER
    303 See Other, see HTTP/1.1 documentation.
    NOT_MODIFIED
    304 Not Modified, see HTTP/1.1 documentation.
    USE_PROXY
    305 Use Proxy, see HTTP/1.1 documentation.
    TEMPORARY_REDIRECT
    307 Temporary Redirect, see HTTP/1.1 documentation.
    BAD_REQUEST
    400 Bad Request, see HTTP/1.1 documentation.
    UNAUTHORIZED
    401 Unauthorized, see HTTP/1.1 documentation.
    PAYMENT_REQUIRED
    402 Payment Required, see HTTP/1.1 documentation.
    FORBIDDEN
    403 Forbidden, see HTTP/1.1 documentation.
    NOT_FOUND
    404 Not Found, see HTTP/1.1 documentation.
    METHOD_NOT_ALLOWED
    405 Method Not Allowed, see HTTP/1.1 documentation.
    NOT_ACCEPTABLE
    406 Not Acceptable, see HTTP/1.1 documentation.
    PROXY_AUTHENTICATION_REQUIRED
    407 Proxy Authentication Required, see HTTP/1.1 documentation.
    REQUEST_TIMEOUT
    408 Request Timeout, see HTTP/1.1 documentation.
    CONFLICT
    409 Conflict, see HTTP/1.1 documentation.
    GONE
    410 Gone, see HTTP/1.1 documentation.
    LENGTH_REQUIRED
    411 Length Required, see HTTP/1.1 documentation.
    PRECONDITION_FAILED
    412 Precondition Failed, see HTTP/1.1 documentation.
    REQUEST_ENTITY_TOO_LARGE
    413 Request Entity Too Large, see HTTP/1.1 documentation.
    REQUEST_URI_TOO_LONG
    414 Request-URI Too Long, see HTTP/1.1 documentation.
    UNSUPPORTED_MEDIA_TYPE
    415 Unsupported Media Type, see HTTP/1.1 documentation.
    REQUESTED_RANGE_NOT_SATISFIABLE
    416 Requested Range Not Satisfiable, see HTTP/1.1 documentation.
    EXPECTATION_FAILED
    417 Expectation Failed, see HTTP/1.1 documentation.
    PRECONDITION_REQUIRED
    428 Precondition required, see RFC 6585: Additional HTTP Status Codes.
    TOO_MANY_REQUESTS
    429 Too Many Requests, see RFC 6585: Additional HTTP Status Codes.
    REQUEST_HEADER_FIELDS_TOO_LARGE
    431 Request Header Fields Too Large, see RFC 6585: Additional HTTP Status Codes.
    INTERNAL_SERVER_ERROR
    500 Internal Server Error, see HTTP/1.1 documentation.
    NOT_IMPLEMENTED
    501 Not Implemented, see HTTP/1.1 documentation.
    BAD_GATEWAY
    502 Bad Gateway, see HTTP/1.1 documentation.
    SERVICE_UNAVAILABLE
    503 Service Unavailable, see HTTP/1.1 documentation.
    GATEWAY_TIMEOUT
    504 Gateway Timeout, see HTTP/1.1 documentation.
    HTTP_VERSION_NOT_SUPPORTED
    505 HTTP Version Not Supported, see HTTP/1.1 documentation.
    NETWORK_AUTHENTICATION_REQUIRED
    511 Network Authentication Required, see RFC 6585: Additional HTTP Status Codes.
     */
}
