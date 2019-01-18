package ru.mityushin.jobfinder.server.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {
    private final Logger log;

    @Pointcut("execution(* ru.mityushin.jobfinder.server.controller.*.*(..))")
    public void controllerMethods() {}

    @Pointcut("execution(* ru.mityushin.jobfinder.server.service.*.*.*(..))")
    public void serviceMethods() {}

    @Around("controllerMethods() || serviceMethods()")
    public Object logMethodCall(ProceedingJoinPoint thisJoinPoint) throws Throwable {
        String methodName = thisJoinPoint.getSignature().getName();
        Object[] methodArgs = thisJoinPoint.getArgs();

        log.debug("Call method " + methodName + " with args: " + Arrays.toString(methodArgs));
        Object result = thisJoinPoint.proceed();
        log.debug("Method " + methodName + " returns: " + result);

        return result;
    }
}
