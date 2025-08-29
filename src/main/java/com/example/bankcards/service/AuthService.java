package com.example.bankcards.service;

import com.example.bankcards.dto.auth.AuthRequest;
import com.example.bankcards.dto.auth.AuthResponse;
import com.example.bankcards.dto.auth.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Сервис для аутентификации и авторизации пользователей.
 * Предоставляет методы для регистрации, входа, обновления токенов и выхода из системы.
 */
public interface AuthService {

    /**
     * Регистрирует нового пользователя в системе.
     * Создает учетную запись пользователя, генерирует JWT токены и устанавливает их в cookies.
     *
     * @param registerRequest запрос на регистрацию, содержащий данные пользователя
     * @param response HTTP ответ для установки cookies с токенами
     * @return ответ аутентификации, содержащий информацию о пользователе и статус операции
     * @throws com.example.bankcards.exception.UserAlreadyExistsException если пользователь с таким email уже существует
     */
    AuthResponse register(RegisterRequest registerRequest, HttpServletResponse response);

    /**
     * Выполняет аутентификацию пользователя в системе.
     * Проверяет учетные данные и генерирует JWT токены при успешной аутентификации.
     *
     * @param authRequest запрос на аутентификацию, содержащий email и пароль
     * @param response HTTP ответ для установки cookies с токенами
     * @return ответ аутентификации, содержащий информацию о пользователе и JWT токены
     * @throws com.example.bankcards.exception.ResourceNotFoundException если пользователь не найден
     */
    AuthResponse login(AuthRequest authRequest, HttpServletResponse response);

    /**
     * Обновляет JWT токены доступа на основе refresh токена.
     * Извлекает refresh токен из cookies и генерирует новую пару токенов.
     *
     * @param request HTTP запрос для получения cookies с токенами
     * @param response HTTP ответ для установки новых cookies с токенами
     * @return ответ аутентификации с обновленными токенами
     * @throws com.example.bankcards.exception.InvalidRefreshTokenException если refresh токен невалиден
     * @throws com.example.bankcards.exception.RefreshTokenMissingException если refresh токен отсутствует
     */
    AuthResponse refreshToken(HttpServletRequest request, HttpServletResponse response);

    /**
     * Выполняет выход пользователя из системы.
     * Удаляет JWT токены из cookies и добавляет refresh токен в черный список.
     *
     * @param request HTTP запрос для получения информации о пользователе и токенах
     * @param response HTTP ответ для удаления cookies с токенами
     */
    void logout(HttpServletRequest request, HttpServletResponse response);
}
