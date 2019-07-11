package com.m3.skinnyrest.annotations;

import java.lang.annotation.Target;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Target(value=ElementType.METHOD)
@Retention(value=RetentionPolicy.RUNTIME)
@HttpMethod(value=HttpMethod.OPTIONS)
@Documented
public @interface OPTIONS {
}
