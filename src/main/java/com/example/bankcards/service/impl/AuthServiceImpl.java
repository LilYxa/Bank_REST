package com.example.bankcards.service.impl;

import com.example.bankcards.dto.auth.AuthRequest;
import com.example.bankcards.dto.auth.AuthResponse;
import com.example.bankcards.dto.auth.RegisterRequest;
import com.example.bankcards.entity.Token;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.entity.enums.TokenType;
import com.example.bankcards.exception.RefreshTokenMissingException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.exception.UserAlreadyExistsException;
import com.example.bankcards.repository.TokenRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.security.service.JwtService;
import com.example.bankcards.util.Constants;
import com.example.bankcards.util.CookieUtil;
import com.example.bankcards.util.mappers.UserMapper;
import com.example.bankcards.util.parser.JwtParser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

/**
 * Реализация сервиса для аутентификации и авторизации пользователей.
 * Предоставляет методы для регистрации, входа, обновления токенов и выхода из системы.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtParser jwtParser;
    private final UserMapper userMapper;

    @Value("${refreshToken.expirationInMs}")
    private Long refreshTokenExpiryMs;

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthResponse register(RegisterRequest registerRequest, HttpServletResponse response) {
        log.debug("register[1]: register user with email: {}", registerRequest.getEmail());
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new UserAlreadyExistsException(Constants.USER_ALREADY_EXISTS);
        }

        var user = User.builder()
                .email(registerRequest.getEmail())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .patronymic(registerRequest.getPatronymic())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.ROLE_USER)
                .enabled(true)
                .accountNonLocked(true)
                .build();

        userRepository.save(user);

        return authenticateInternal(registerRequest.getEmail(), registerRequest.getPassword(), response);
    }

    /**
     * {@inheritDoc}
     */
    public AuthResponse login(AuthRequest request, HttpServletResponse response) {
        return authenticateInternal(request.getEmail(), request.getPassword(), response);
    }

    private AuthResponse authenticateInternal(String email, String password, HttpServletResponse response) {
        log.debug("authenticateInternal[1]: authenticate user with email: {}", email);
        var authToken = new UsernamePasswordAuthenticationToken(email, password);
        authManager.authenticate(authToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.USER_RESOURCE));

        var claims = Map.<String ,Object>of(Constants.ROLE_CLAIM, user.getRole().name());
        String accessToken = jwtService.generateAccessToken(user.getEmail(), claims);
        String refreshTokenStr = jwtService.generateRefreshToken(user.getEmail(), claims);

        Token refreshToken = Token.builder()
                .token(refreshTokenStr)
                .type(TokenType.REFRESH)
                .user(user)
                .revoked(false)
                .expired(false)
                .expiresAt(Instant.now().plusMillis(refreshTokenExpiryMs))
                .build();
        tokenRepository.save(refreshToken);
        log.debug("authenticateInternal[2]: refresh token was saved: {}", refreshTokenStr);

        CookieUtil.makeRefreshCookie(response, refreshTokenStr, (int) (refreshTokenExpiryMs / 1000));
        return AuthResponse.builder()
                .accessToken(accessToken)
                .tokenType(Constants.BEARER_TOKEN)
                .user(userMapper.userToUserDto(user))
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {
        log.debug("refreshToken[1]: Refresh tokens");
        String refreshCookie = CookieUtil.extractRefreshTokenFromCookie(request);
        log.debug("refreshToken[2]: refresh cookie: {}", refreshCookie);
        if (refreshCookie == null) {
            throw new RefreshTokenMissingException(Constants.REFRESH_TOKEN_MISSING);
        }

        var tokenOpt = tokenRepository.findByToken(refreshCookie);
        if (tokenOpt.isEmpty()) throw new RefreshTokenMissingException(Constants.REFRESH_TOKEN_MISSING);

        var stored = tokenOpt.get();
        if (stored.isRevoked() || stored.isExpired() || jwtParser.isTokenExpired(refreshCookie)) {
            throw new IllegalArgumentException(Constants.INVALID_REFRESH_TOKEN_MSG);
        }

        var user = stored.getUser();
        log.debug("refreshToken[3]: user: {}", user);
        Map<String, Object> extraClaims = Map.of(Constants.ROLE_CLAIM, user.getRole().name());

        stored.setRevoked(true);
        stored.setExpired(true);
        tokenRepository.save(stored);

        String newAccess = jwtService.generateAccessToken(user.getEmail(), extraClaims);
        String newRefresh = jwtService.generateRefreshToken(user.getEmail(), extraClaims);

        Token newRefreshToken = Token.builder()
                .token(newRefresh)
                .type(TokenType.REFRESH)
                .user(user)
                .revoked(false)
                .expired(false)
                .expiresAt(Instant.now().plusMillis(refreshTokenExpiryMs))
                .build();
        tokenRepository.save(newRefreshToken);

        CookieUtil.makeRefreshCookie(response, newRefresh, (int) (refreshTokenExpiryMs / 1000));

        return AuthResponse.builder()
                .accessToken(newAccess)
                .user(userMapper.userToUserDto(user))
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        log.debug("logout[1]: Logout");
        String refreshCookie = CookieUtil.extractRefreshTokenFromCookie(request);
        log.debug("logout[1]: refresh cookie: {}", refreshCookie);
        if (refreshCookie != null) {
            tokenRepository.findByToken(refreshCookie).ifPresent(t -> {
                t.setRevoked(true);
                t.setExpired(true);
                tokenRepository.save(t);
            });
        }
        CookieUtil.clearRefreshCookie(response);
    }
}
