package ru.mityushin.jobfinder.server.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    @Pointcut("execution(* ru.mityushin.jobfinder.server.controller.*.*(..))")
    public void controllerMethods() {
    }

    @Pointcut("execution(* ru.mityushin.jobfinder.server.service.*.*.*(..))")
    public void serviceMethods() {
    }

    @Around("controllerMethods() || serviceMethods()")
    public Object logMethodCall(ProceedingJoinPoint thisJoinPoint) throws Throwable {
        Logger log = LoggerFactory.getLogger(thisJoinPoint.getTarget().getClass());
        String methodName = thisJoinPoint.getSignature().getName();
        String methodArgs = Arrays.toString(thisJoinPoint.getArgs());
        Class returnType = ((MethodSignature) thisJoinPoint.getSignature()).getReturnType();
        log.debug("Call method {} with args: {}", methodName, methodArgs);

        long startTime = System.currentTimeMillis();
        Object result = thisJoinPoint.proceed();
        long finishTime = System.currentTimeMillis() - startTime;

        if (returnType.equals(Void.TYPE)) {
            log.debug("Method {} run {} millis", methodName, finishTime);
        } else {
            log.debug("Method {} run {} millis. Returns: {}", methodName, finishTime, result);
        }
        return result;
    }
}
