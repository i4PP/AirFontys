package me.abdul.authentication.Dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import me.abdul.authentication.validtions.EmailValidation;
import me.abdul.authentication.validtions.PasswordValidation;

@Getter
@Setter
public class RegisterDto {

    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    @NotBlank(message = "Email is required")
    @EmailValidation
    private String email;
    @NotBlank(message = "Password is required")
    private String password;

}