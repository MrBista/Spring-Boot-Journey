package com.bisma.foundation.aop_learn.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.bisma.foundation.aop_learn.controller.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {

        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getName();
        Object[] args = joinPoint.getArgs();


        log.info("[BEFORE] {}.{}() dipnaggil dengan args {}",
                className, methodName, Arrays.toString(args));
    }


    @Around("execution(* com.bisma.foundation.aop_learn.service.*.*(..))")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().getName();

        long start = System.currentTimeMillis();
        log.info("[AROUND] waktu start eksekusi {}", start);

        Object result;
        try{
            result = pjp.proceed();
        } catch (Throwable e) {
            log.error("[AROUND] Exception di {}:{}", methodName, e.getMessage());
            throw e;
        }finally {
            long elapsed = System.currentTimeMillis() - start;
            log.info("[AROUND] waktu selesai eksekusi {}", elapsed);
        }

        return result;
    }


}
