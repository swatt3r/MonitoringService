package org.monitoringservice.util.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.monitoringservice.dto.UserDTO;
import org.monitoringservice.entities.TypeOfAction;
import org.monitoringservice.services.AuditService;

/**
 * Класс аспекта. Используется для аудита во время авторизации и регистрации.
 */
@Aspect
public class AuthAuditAspect {
    /**
     * Метод, обрабатывающий аудит во время авторизации.
     *
     * @param proceedingJoinPoint  место в коде, где работает метод
     * @return результат работы метода
     */
    @Around("execution(* org.monitoringservice.services.AuthenticationService.login(..))")
    public Object loginAudit(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        Object result = proceedingJoinPoint.proceed();
        if(result instanceof UserDTO){
            AuditService.saveAction(((UserDTO) result).getLogin(), 0,TypeOfAction.Login);
        }
        return result;
    }
    /**
     * Метод, обрабатывающий аудит во время регистрации.
     *
     * @param proceedingJoinPoint  место в коде, где работает метод
     */
    @Around("execution(* org.monitoringservice.services.AuthenticationService.register(..))")
    public void registerAudit(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        proceedingJoinPoint.proceed();
        String login = (String) proceedingJoinPoint.getArgs()[0];
        AuditService.saveAction(login,0, TypeOfAction.Register);
    }
}
