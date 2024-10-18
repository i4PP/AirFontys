package me.abdul.authentication.validtions;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<EmailValidation, String> {
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return email != null && email.matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$");
    }
}
