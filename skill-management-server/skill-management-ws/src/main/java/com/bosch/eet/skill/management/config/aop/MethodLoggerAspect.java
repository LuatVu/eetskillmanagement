package com.bosch.eet.skill.management.config.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Slf4j
public class MethodLoggerAspect {

    @Pointcut("execution(* com.bosch.eet.skill.management.service.impl.SupplyDemandServiceImpl.*(..))" +
            "||execution(* com.bosch.eet.skill.management.service.impl.ObjectStorageServiceImpl.*(..))")
    public void classMethods() {
    }

    @Around("classMethods()")
    public Object logMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        log.info("Entering method: {}()", methodName);

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - startTime;

        log.info("Exiting method: {}(): execution time={}ms", methodName, executionTime);
        return result;
    }
}
