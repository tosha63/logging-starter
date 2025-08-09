package ru.shtanko.logginstarter.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import ru.shtanko.logginstarter.annotation.LogExecutionTime;

import java.lang.reflect.Method;
import java.util.Optional;

@Aspect
public class LogExecutionAspect {

    private static final Logger log = LoggerFactory.getLogger(LogExecutionAspect.class);

    @Around("@annotation(ru.shtanko.logginstarter.annotation.LogExecutionTime)")
    public Object aroundLogExecutionTimeMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        LogExecutionTime originalAnnotation = method.getAnnotation(LogExecutionTime.class);
        String methodName = Optional.ofNullable(originalAnnotation)
               .map(LogExecutionTime::methodName)
               .filter(StringUtils::hasText)
               .orElse(method.getName());

        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            throw e.getCause();
        } finally {
            log.info("Время выполнения метода {}: {}", methodName, (System.currentTimeMillis() - start));
        }
    }
}
