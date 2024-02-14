package org.monitoringservice.util.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.monitoringservice.services.LogService;

/**
 * Класс аспекта. Используется для логгирования выполнения методов.
 */
@Aspect
public class LoggableAspect {
    /**
     * Метод, обрабатывающий логгирование выполнения методов.
     *
     * @param proceedingJoinPoint место в коде, где работает метод
     * @return результат работы метода
     */
    @Around("execution(* *(..))")
    public Object logging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        LogService.log("Calling method" + proceedingJoinPoint.getSignature());
        long start = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long time = System.currentTimeMillis() - start;
        LogService.log("Method" + proceedingJoinPoint.getSignature() + "finished." + " Execution time is: " + time);
        return result;
    }
}