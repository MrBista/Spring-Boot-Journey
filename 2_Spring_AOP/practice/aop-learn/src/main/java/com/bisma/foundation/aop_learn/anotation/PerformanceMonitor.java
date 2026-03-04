package com.bisma.foundation.aop_learn.anotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PerformanceMonitor {
    long warnThresholdMs() default 1000;
}
