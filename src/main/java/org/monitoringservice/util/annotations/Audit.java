package org.monitoringservice.util.annotations;

import org.monitoringservice.entities.TypeOfAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для аудита.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Audit {
    TypeOfAction typeOfAction();

    boolean haveLogin();
    int identifierPos();
}
