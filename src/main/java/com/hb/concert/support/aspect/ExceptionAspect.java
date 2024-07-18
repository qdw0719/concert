package com.hb.concert.support.aspect;

import com.hb.concert.domain.exception.CustomException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect @Component
public class ExceptionAspect {

    @AfterThrowing(pointcut = "execution(* com.hb.concert..*(..))", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        if (ex instanceof CustomException) {
            ((CustomException) ex).setMethod(joinPoint.getSignature().getName());
            ((CustomException) ex).setClassName(joinPoint.getSignature().getDeclaringTypeName());
        }
    }
}
