package me.abdul.authentication.validtions;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PasswordValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordValidation {

    String message() default "Password must contain at least 8 characters, 1 uppercase letter, 1 lowercase letter, 1 number and 1 special character";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
