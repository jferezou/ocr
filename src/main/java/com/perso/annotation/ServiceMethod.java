package com.perso.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface ServiceMethod {
    String DEFAULT_METHOD_NAME = "";

    String value() default "";

    boolean traceTime() default true;

    boolean traceArgs() default true;
}