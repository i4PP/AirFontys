package me.abdul.authentication.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import me.abdul.authentication.Dtos.LoginResultDto;
import me.abdul.authentication.Dtos.LoginUserDto;
import me.abdul.authentication.Dtos.RegisterDto;
import me.abdul.authentication.Dtos.RegisterResultDto;
import me.abdul.authentication.entities.Auth;
import me.abdul.authentication.entities.RefreshToken;
import me.abdul.authentication.entities.User;
import me.abdul.authentication.exceptions.InvalidRefreshTokenEception;
import me.abdul.authentication.services.AuthService;
import me.abdul.authentication.services.JwtService;
import me.abdul.authentication.services.RefreshTokenService;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Logout user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully logged out user"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue("refresh-token") String refreshToken) {
        RefreshToken token = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidRefreshTokenEception("Invalid refresh token"));
        refreshTokenService.markAsUsed(token);

        ResponseCookie jwtCookie = createCookie("auth-token", "");
        ResponseCookie refreshTokenCookie = createCookie("refresh-token", "");

        return ResponseEntity.ok()
                .header("Set-Cookie", jwtCookie.toString())
                .header("Set-Cookie", refreshTokenCookie.toString())
                .body("Successfully logged out");

    }


    @Operation(summary = "Login user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully logged in user"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/login")
    public ResponseEntity<Auth> login(@RequestBody LoginUserDto loginUserDto) {
        LoginResultDto loginResultDto = authService.authenticate(loginUserDto);
        return createAuthResponse(loginResultDto.getJwtToken(), loginUserDto.getEmail(), loginResultDto);
    }

    @Operation(summary = "Register user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully registered user"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "User already exists"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    @PostMapping("/register")
    public ResponseEntity<Auth> register(@Valid @RequestBody RegisterDto registerDto) {
        RegisterResultDto result = authService.register(registerDto);
        return createAuthResponse(result.getJwtToken(), registerDto.getEmail(), result);
    }

    @Operation(summary = "Refresh token")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully refreshed token", content = {}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid refresh token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/refresh")
    public ResponseEntity<Auth> refresh(@CookieValue("refresh-token") String refreshToken) {
        RefreshToken token = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidRefreshTokenEception("Invalid refresh token"));
        refreshTokenService.verifyExpiration(token);

        User user = token.getUser();
        String jwtToken = jwtService.generateToken(user);
        LoginResultDto loginResultDto = new LoginResultDto();
        loginResultDto.setUser(user);

        return createAuthResponse(jwtToken, user.getEmail(), loginResultDto);
    }


    private ResponseEntity<Auth> createAuthResponse(String jwtToken, String email, Object dto) {
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(email);

        ResponseCookie jwtCookie = createCookie("auth-token", jwtToken);
        ResponseCookie refreshTokenCookie = createCookie("refresh-token", refreshToken.getToken());

        Auth authResponse;
        if (dto instanceof LoginResultDto loginResultDto) {
            authResponse = new Auth(loginResultDto);
        } else if (dto instanceof RegisterResultDto registerResultDto) {
            authResponse = new Auth(registerResultDto);
        } else {
            throw new IllegalArgumentException("Unsupported DTO type");
        }

        return ResponseEntity.ok()
                .header("Set-Cookie", jwtCookie.toString())
                .header("Set-Cookie", refreshTokenCookie.toString())
                .body(authResponse);
    }

    // create cookies
    private ResponseCookie createCookie(String name, String value) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true) // Change to true when using a valid SSL certificate
                .path("/")
                .maxAge(3600)
                .sameSite("Strict")
                .build();
    }
}
