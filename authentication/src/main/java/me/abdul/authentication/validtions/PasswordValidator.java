    package me.abdul.authentication.validtions;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<PasswordValidation, String> {

    private static final String PASSWORD_PATTERN =
            "^(?=.*[\\d])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";


    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        // Check if the password matches the regular expression
        return password.matches(PASSWORD_PATTERN);
    }
}