package com.bisma.foundation.aop_learn.anotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Loggable {
    String value() default "";
    boolean logArgs() default true;
    boolean logResult() default true;

}
