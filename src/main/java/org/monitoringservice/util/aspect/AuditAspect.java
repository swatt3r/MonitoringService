package org.monitoringservice.util.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.monitoringservice.entities.TypeOfAction;
import org.monitoringservice.services.AuditService;
import org.monitoringservice.util.annotations.Audit;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Класс аспекта. Используется для аудирования.
 */
@Aspect
@Component
public class AuditAspect {
    /**
     * Метод, обрабатывающий аудирование.
     *
     * @param proceedingJoinPoint место в коде, где работает метод
     * @return результат работы метода
     */
    @Around("@annotation(org.monitoringservice.util.annotations.Audit)")
    public Object audit(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();
        Audit annotation = method.getAnnotation(Audit.class);

        Object result = proceedingJoinPoint.proceed();

        String login = "";
        int id = -1;
        TypeOfAction type = annotation.typeOfAction();
        int pos = annotation.identifierPos();

        if(pos == -1){
            AuditService.saveAction("admin", id, type);
            return result;
        }

        if(annotation.haveLogin()){
            login = (String) proceedingJoinPoint.getArgs()[0];
        }else {
            try {
                id = Integer.parseInt((String) proceedingJoinPoint.getArgs()[0]);
            }
            catch (NumberFormatException e){
                System.out.println(e.getMessage());
            }
        }

        AuditService.saveAction(login, id, type);
        return result;
    }
}