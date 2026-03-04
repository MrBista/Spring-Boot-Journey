package com.bisma.foundation.aop_learn.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
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
        /*
            Berjalan sebelum method target
            Tidak bisa mencegah eksekusi method (kecuali throw exception)
            Tidak bisa memodifikasi return value
         */

        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getName();
        Object[] args = joinPoint.getArgs();


        log.info("[BEFORE] {}.{}() dipnaggil dengan args {}",
                className, methodName, Arrays.toString(args));
    }


    @Around("execution(* com.bisma.foundation.aop_learn.service.*.*(..))")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        /*
            Paling fleksibel — bisa lakukan sebelum DAN sesudah
            Bisa mencegah eksekusi method asli (tidak panggil pjp.proceed())
            Bisa memodifikasi args sebelum diteruskan: pjp.proceed(newArgs)
            Bisa memodifikasi return value
            Wajib return Object dan throw Throwable

         */
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

    @AfterReturning(
            pointcut = "execution(* com.bisma.foundation.aop_learn.controller.*.*(..))",
            returning = "res"

    )
    public void logReturningVal(JoinPoint jp, Object res) {
        /*
            Hanya berjalan jika method tidak throw exception
            Bisa mengakses return value via parameter returning
            Tidak bisa mengubah return value
         */
        log.info("[AFTER RETURNING] method {} return value {}",jp.getSignature().getName(), res);
    }


    @AfterThrowing(
            pointcut = "execution(* com.bisma.foundation.aop_learn.controller.*.*(..))",
            throwing = "ex"
    )
    public void logErrorThrow(JoinPoint jp, Exception ex) {
        log.error("[AFTER THROWING] method {} melempar error {}", jp.getSignature().getName(), ex.getMessage());
    }




}
