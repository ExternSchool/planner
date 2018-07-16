package io.github.externschool.planner.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Pointcut("within(io.github.externschool.planner.controller..*) || within(io.github.externschool.planner.service..*) ")
    private void allMethods() {}

    @Before("allMethods()")
    public void log(final JoinPoint joinPoint) {
        log.info("Called {} with arguments:", joinPoint.getSignature().toShortString());
        for (final Object argument : joinPoint.getArgs()) {
            log.info("  {}", argument);
        }
    }

    @Around("allMethods()")
    public Object logBusinessMethods(final ProceedingJoinPoint joinPoint) throws Throwable {
        if (!log.isDebugEnabled()) {

            return joinPoint.proceed();
        } else {
            Object[] args = joinPoint.getArgs();
            String message = joinPoint.toShortString();
            log.info("{} called with args '{}'!", message, Arrays.deepToString(args));
            Object result = null;

            try {
                result = joinPoint.proceed();

                return result;
            } finally {
                MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
                if (methodSignature.getReturnType() == Void.TYPE) {
                    result = "void";
                }
                String returnMessage = message.replace("execution", "comeback");
                log.info("{} return '{}'!", returnMessage, result);
            }
        }
    }

    @Around("@annotation(LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        log.info("{} executed in {} ms", joinPoint.getSignature().toShortString(), executionTime);

        return proceed;
    }

    @AfterThrowing(pointcut = "allMethods()", throwing = "ex")
    public void logAfter(final JoinPoint joinPoint, final Exception ex) {
        log.error("Thrown by {}: {}",
                joinPoint.getSignature().toShortString(),
                ex.getMessage());
    }
}
