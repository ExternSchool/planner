package io.github.externschool.planner.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
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
        Object[] arguments = joinPoint.getArgs();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        System.out.println("before" + className + "." + methodName + "() -----");
        for (final Object argument : arguments) {
            System.out.println(argument);
        }
    }

    @Around("allMethods()")
    public Object logBusinessMethods(final ProceedingJoinPoint call) throws Throwable {
        if (!log.isDebugEnabled()) {
            return call.proceed();
        } else {
            Object[] args = call.getArgs();
            String message = call.toShortString();
            log.info("{} called with args '{}'!", message, Arrays.deepToString(args));
            Object result = null;

            try {
                result = call.proceed();

                return result;
            } finally {
                MethodSignature methodSignature = (MethodSignature) call.getSignature();
                if (methodSignature.getReturnType() == Void.TYPE) {
                    result = "void";
                }
                String returnMessage = message.replace("execution", "comeback");
                log.info("{} return '{}'!", returnMessage, result);
            }
        }
    }

    @After("allMethods()")
    public void logAfter(final JoinPoint joinPoint) {
        log.info("after: "+joinPoint.getTarget().getClass().getSimpleName()+" "+joinPoint.getSignature().getName());
    }
}
