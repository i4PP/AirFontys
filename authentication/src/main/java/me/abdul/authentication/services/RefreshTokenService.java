package me.abdul.authentication.services;

import me.abdul.authentication.entities.RefreshToken;
import me.abdul.authentication.exceptions.InvalidRefreshTokenEception;
import me.abdul.authentication.repos.RefreshTokenRepository;
import me.abdul.authentication.repos.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${refresh.token.Duration}")
    private int refreshTokenDuration;

    final
    RefreshTokenRepository refreshTokenRepository;

    final
    UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public RefreshToken createRefreshToken(String email){
        RefreshToken refreshToken = RefreshToken.builder()
                .user(userRepository.findByEmail(email).orElseThrow())
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusSeconds(refreshTokenDuration))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }


    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }

    public void markAsUsed(RefreshToken token){
        if(token.isUsed()){
            List<RefreshToken> refreshTokens = refreshTokenRepository.findByUserId(token.getUser().getId()).orElseThrow();
            refreshTokens.forEach(refreshToken -> refreshToken.setReuseFlag(true));
            refreshTokenRepository.saveAll(refreshTokens);
        }
        else {
            token.setUsed(true);
            refreshTokenRepository.save(token);
        }
    }

    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.getExpiryDate().compareTo(Instant.now())<0 || token.isReuseFlag() || token.isUsed()){
            List<RefreshToken> refreshTokens = refreshTokenRepository.findByUserId(token.getUser().getId()).orElseThrow();
            refreshTokens.forEach(refreshToken -> refreshToken.setReuseFlag(true));
            refreshTokenRepository.saveAll(refreshTokens);
            throw new InvalidRefreshTokenEception("Invalid refresh token");
        }
        token.setUsed(true);
        refreshTokenRepository.save(token);
        return token;
    }

}