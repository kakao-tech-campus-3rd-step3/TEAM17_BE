package com.starterpack.hashtag.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = HashtagValidator.class)
public @interface HashtagCheck {
    String message() default "잘못된 해시태그 양식입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
