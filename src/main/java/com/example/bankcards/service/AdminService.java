package com.example.bankcards.service;

import com.example.bankcards.dto.auth.RegisterRequest;
import com.example.bankcards.dto.card.BalanceUpdateRequest;
import com.example.bankcards.dto.card.CardCreateRequest;
import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.dto.user.UserUpdateRequest;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Сервис для административных операций управления пользователями и картами.
 * Предоставляет методы для работы с пользовательскими аккаунтами и банковскими картами
 * с правами администратора системы.
 */
import java.util.UUID;

public interface AdminService {

    /**
     * Получает страницу со всеми банковскими картами в системе.
     *
     * @param pageable параметры пагинации и сортировки
     * @return страница с DTO банковских карт
     */
    Page<CardDto> getAllCards(Pageable pageable);

    /**
     * Обновляет баланс банковской карты.
     *
     * @param request запрос на обновление баланса с идентификатором карты и новым значением баланса
     * @return DTO обновленной банковской карты
     * @throws com.example.bankcards.exception.ResourceNotFoundException если карта не найдена
     */
    CardDto updateCardBalance(BalanceUpdateRequest request);

    /**
     * Создает нового пользователя в системе.
     *
     * @param request запрос на регистрацию с данными пользователя
     * @return DTO созданного пользователя
     * @throws com.example.bankcards.exception.UserAlreadyExistsException если пользователь с таким email уже существует
     */
    UserDto createUser(RegisterRequest request);

    /**
     * Получает страницу со всеми пользователями системы.
     *
     * @param pageable параметры пагинации и сортировки
     * @return страница с DTO пользователей
     */
    Page<UserDto> getAllUsers(Pageable pageable);

    /**
     * Получает пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя
     * @return DTO пользователя
     * @throws com.example.bankcards.exception.ResourceNotFoundException если пользователь не найден
     */
    UserDto getUserById(UUID userId);

    /**
     * Обновляет статус активности пользователя.
     *
     * @param userId идентификатор пользователя
     * @param enabled true - аккаунт активен, false - аккаунт отключен
     * @throws com.example.bankcards.exception.ResourceNotFoundException если пользователь не найден
     * @return DTO обновленного пользователя
     */
    UserDto updateUserStatus(UUID userId, boolean enabled);

    /**
     * Обновляет статус блокировки пользовательского аккаунта.
     *
     * @param userId идентификатор пользователя
     * @param accountNonLocked true - аккаунт не заблокирован, false - аккаунт заблокирован
     * @return DTO обновленного пользователя
     * @throws com.example.bankcards.exception.ResourceNotFoundException если пользователь не найден
     */
    UserDto updateUserLockStatus(UUID userId, boolean accountNonLocked);

    /**
     * Обновляет данные пользователя.
     *
     * @param userId идентификатор пользователя
     * @param request запрос с обновляемыми данными пользователя
     * @return DTO обновленного пользователя
     * @throws com.example.bankcards.exception.ResourceNotFoundException если пользователь не найден
     * @throws com.example.bankcards.exception.UserAlreadyExistsException если пользователь с новым email уже существует
     */
    UserDto updateUser(UUID userId, UserUpdateRequest request);

    /**
     * Удаляет пользователя из системы.
     *
     * @param userId идентификатор пользователя для удаления
     * @throws com.example.bankcards.exception.ResourceNotFoundException если пользователь не найден
     */
    void deleteUser(UUID userId);
}
