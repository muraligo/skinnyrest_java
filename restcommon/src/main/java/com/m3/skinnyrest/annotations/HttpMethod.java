package com.m3.skinnyrest.annotations;

import java.lang.annotation.Target;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Target(value=ElementType.ANNOTATION_TYPE)
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface HttpMethod {
    String value();

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final String HEAD = "HEAD";
    public static final String OPTIONS = "OPTIONS";
}
