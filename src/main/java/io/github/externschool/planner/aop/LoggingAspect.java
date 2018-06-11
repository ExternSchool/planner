package io.github.externschool.planner.aop;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.aspectj.lang.reflect.MethodSignature;


import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Pointcut("within(io.github.externschool.planner.controller..*) || within(io.github.externschool.planner.service..*) ")
    private void allMethods() {

    }

        @Before("allMethods()")
    public void log(JoinPoint joinPoint) {
        Object[] arguments = joinPoint.getArgs();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        System.out.println("before" + className + "." + methodName + "() -----");

        for (int i = 0; i < arguments.length; i++) {
            System.out.println(arguments[i]);
        }
    }
    @Around("allMethods()")
    public Object logBusinessMethods(ProceedingJoinPoint call) throws Throwable {
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
   public void logAfter(JoinPoint joinPoint) {
            log.info("after: "+joinPoint.getTarget().getClass().getSimpleName()+" "+joinPoint.getSignature().getName());

    }


}




