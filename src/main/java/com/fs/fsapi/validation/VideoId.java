package com.fs.fsapi.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = VideoIdValidator.class)
@Documented
public @interface VideoId {
  
  String message() default "Invalid video id";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
