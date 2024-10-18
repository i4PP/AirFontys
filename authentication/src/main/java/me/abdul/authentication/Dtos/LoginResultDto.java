package me.abdul.authentication.Dtos;

import lombok.Getter;
import lombok.Setter;
import me.abdul.authentication.entities.User;


@Getter
@Setter
public class LoginResultDto {
    private String jwtToken;
    private String refreshToken;
    private User user;
}