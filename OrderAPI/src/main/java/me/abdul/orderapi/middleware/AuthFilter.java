package me.abdul.orderapi.middleware;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import me.abdul.orderapi.services.JwtService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authToken = extractAuthTokenFromCookies(request);

        if (authToken != null) {
            handleAuthentication(authToken, request, response);
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }


    }

    private String extractAuthTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("auth-token")) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    private void handleAuthentication(String authToken, HttpServletRequest request, HttpServletResponse response) {

        if (!jwtService.isTokenValid(authToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            String user = jwtService.extractClaim(authToken, Claims::getSubject);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(user , null, null);

            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(auth);
        }


    }
}
