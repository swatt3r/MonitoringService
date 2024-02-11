package org.monitoringservice.util.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.monitoringservice.entities.TypeOfAction;
import org.monitoringservice.services.AuditService;

/**
 * Класс аспекта. Используется для аудита во время работы с сервисом счетчиков.
 */
@Aspect
public class MeterAuditAspect {
    /**
     * Метод, обрабатывающий аудит во время получения истории показаний.
     *
     * @param proceedingJoinPoint место в коде, где работает метод
     * @return результат работы метода
     */
    @Around("execution(* org.monitoringservice.services.MeterService.getUserHistory(..))")
    public Object historyAudit(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        Integer id = (Integer) proceedingJoinPoint.getArgs()[0];
        AuditService.saveAction("", id, TypeOfAction.History);
        return result;
    }

    /**
     * Метод, обрабатывающий аудит во время получения актуальной истории показаний.
     *
     * @param proceedingJoinPoint место в коде, где работает метод
     * @return результат работы метода
     */
    @Around("execution(* org.monitoringservice.services.MeterService.getUserActual(..))")
    public Object actualAudit(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        Integer id = (Integer) proceedingJoinPoint.getArgs()[0];
        AuditService.saveAction("", id, TypeOfAction.Actual);
        return result;
    }

    /**
     * Метод, обрабатывающий аудит во время добавления нового типа счетика пользователю.
     *
     * @param proceedingJoinPoint место в коде, где работает метод
     */
    @Around("execution(* org.monitoringservice.services.MeterService.addNewMeterToUser(..))")
    public void newMeterAudit(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        proceedingJoinPoint.proceed();
        Integer id = (Integer) proceedingJoinPoint.getArgs()[0];
        AuditService.saveAction("", id, TypeOfAction.AddMeter);
    }

    /**
     * Метод, обрабатывающий аудит во время добавления нового типа счетика.
     *
     * @param proceedingJoinPoint место в коде, где работает метод
     */
    @Around("execution(* org.monitoringservice.services.MeterService.addNewType(..))")
    public void newMeterTypeAudit(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        proceedingJoinPoint.proceed();
        AuditService.saveAction("admin", 0, TypeOfAction.NewMeter);
    }

    /**
     * Метод, обрабатывающий аудит во время получения списка счетчиков пользователя.
     *
     * @param proceedingJoinPoint место в коде, где работает метод
     */
    @Around("execution(* org.monitoringservice.services.MeterService.getUserMeters(..))")
    public void showUserMeterAudit(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        proceedingJoinPoint.proceed();
        Integer id = (Integer) proceedingJoinPoint.getArgs()[0];
        AuditService.saveAction("", id, TypeOfAction.ShowUserMeter);
    }

    /**
     * Метод, обрабатывающий аудит во время получения истории показаний за месяц.
     *
     * @param proceedingJoinPoint место в коде, где работает метод
     */
    @Around("execution(* org.monitoringservice.services.MeterService.getUserMonthHistory(..))")
    public void monthHistoryAudit(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        proceedingJoinPoint.proceed();
        Integer id = (Integer) proceedingJoinPoint.getArgs()[0];
        AuditService.saveAction("", id, TypeOfAction.MonthHistory);
    }

    /**
     * Метод, обрабатывающий аудит во время добавления нового показания.
     *
     * @param proceedingJoinPoint место в коде, где работает метод
     */
    @Around("execution(* org.monitoringservice.services.MeterService.newReadout(..))")
    public void newReadoutAudit(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        proceedingJoinPoint.proceed();
        Integer id = (Integer) proceedingJoinPoint.getArgs()[0];
        AuditService.saveAction("", id, TypeOfAction.Readout);
    }
}