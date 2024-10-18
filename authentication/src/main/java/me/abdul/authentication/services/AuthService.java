package me.abdul.authentication.services;


import me.abdul.authentication.Dtos.LoginResultDto;
import me.abdul.authentication.Dtos.LoginUserDto;
import me.abdul.authentication.Dtos.RegisterDto;
import me.abdul.authentication.Dtos.RegisterResultDto;
import me.abdul.authentication.entities.User;
import me.abdul.authentication.exceptions.InvalidCredentialsException;
import me.abdul.authentication.exceptions.UserAlreadyExistException;
import me.abdul.authentication.repos.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Value("${security.password.pepper}")
    private String pepper;

    public AuthService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = new BCryptPasswordEncoder(10);
    }

    public RegisterResultDto register(RegisterDto input) {
        if (userRepository.existsByEmail(input.getEmail())) {
            // Throw a custom exception if user already exists
            throw new UserAlreadyExistException("A user with the given email already exists.");
        }
        User user = new User();

        user.setFirstName(input.getFirstName());
        user.setLastName(input.getLastName());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword() + pepper));

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        RegisterResultDto registerResultDto = new RegisterResultDto();
        registerResultDto.setJwtToken(token);
        registerResultDto.setUser(user);
        return registerResultDto;
    }


    public LoginResultDto authenticate(LoginUserDto input) {
        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (passwordEncoder.matches((input.getPassword() + pepper), user.getPassword())) {
            String token = jwtService.generateToken(user);
            LoginResultDto loginResultDto = new LoginResultDto();
            loginResultDto.setJwtToken(token);
            loginResultDto.setUser(user);
            return loginResultDto;
        }

        throw new InvalidCredentialsException("Invalid credentials");
    }
}