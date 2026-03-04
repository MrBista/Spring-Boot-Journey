package com.bisma.foundation.aop_learn.aop;

import com.bisma.foundation.aop_learn.anotation.PerformanceMonitor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MonitorAspect {

    private static final Logger log = LoggerFactory.getLogger(MonitorAspect.class);



    @Around("@annotation(performanceMonitor)")
    public Object monitorMethod(ProceedingJoinPoint pjp, PerformanceMonitor performanceMonitor) throws Throwable {
        long start = System.currentTimeMillis();

        String methodName = pjp.getSignature().getName();

        Object result;

        try {
            result = pjp.proceed();
        }finally {
            long elapsed = System.currentTimeMillis() - start;
            if (elapsed > performanceMonitor.warnThresholdMs()) {
                log.warn("[PERF] method {} lambat melebihi treshold {} dengan kecepatan {}",
                        methodName,
                        performanceMonitor.warnThresholdMs(),
                        elapsed
                        );
            }else {
                log.info("[PERF] method {} selesai dalam waktu {}", methodName, elapsed);

            }
        }

        return result;
    }
}
