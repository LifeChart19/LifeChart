package org.example.lifechart.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.example.lifechart.validation.validator.TagsValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TagsValidator.class)
@Documented
public @interface ValidTags {
	String message() default "태그는 명사로만 작성해야 하고, 제목의 주요 키워드들을 모두 포함해야 하며, "
		+ "(한글, 영문, 숫자)로만 구성되어야 합니다";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
