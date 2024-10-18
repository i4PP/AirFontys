package me.abdul.authentication.entities;

import lombok.Getter;
import lombok.Setter;
import me.abdul.authentication.Dtos.LoginResultDto;
import me.abdul.authentication.Dtos.RegisterResultDto;

@Getter
@Setter
public class Auth {
    private int id;
    private String firstName;
    private String lastName;
    private String email;


    public Auth(LoginResultDto loginResultDto) {
        this.id = loginResultDto.getUser().getId();
        this.firstName = loginResultDto.getUser().getFirstName();
        this.lastName = loginResultDto.getUser().getLastName();
        this.email = loginResultDto.getUser().getEmail();


    }

    public Auth(RegisterResultDto registerResultDto) {
        this.id = registerResultDto.getUser().getId();
        this.firstName = registerResultDto.getUser().getFirstName();
        this.lastName = registerResultDto.getUser().getLastName();
        this.email = registerResultDto.getUser().getEmail();

    }
}
