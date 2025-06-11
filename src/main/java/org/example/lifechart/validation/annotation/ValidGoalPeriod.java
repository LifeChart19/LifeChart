package org.example.lifechart.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.example.lifechart.validation.validator.GoalPeriodValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = GoalPeriodValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidGoalPeriod {
	String message() default "목표 시작일과 종료일이 유효하지 않습니다.";
	Class<?> [] groups() default {};
	Class<? extends Payload> [] payload() default {};
}
