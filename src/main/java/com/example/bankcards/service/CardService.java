package com.example.bankcards.service;

import com.example.bankcards.dto.card.BalanceResponse;
import com.example.bankcards.dto.card.CardCreateRequest;
import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Сервис для управления банковскими картами пользователей.
 * Предоставляет методы для создания, получения, блокировки, активации и удаления карт,
 * а также для работы с балансами карт.
 */
public interface CardService {

    /**
     * Создает новую банковскую карту для указанного пользователя.
     * Генерирует уникальный номер карты, шифрует его и сохраняет только последние 4 цифры.
     *
     * @param cardCreateRequest запрос на создание карты с данными владельца и сроком действия
     * @return DTO созданной карты
     * @throws com.example.bankcards.exception.ResourceNotFoundException если пользователь не найден
     */
    CardDto createCard(CardCreateRequest cardCreateRequest);

    /**
     * Получает страницу с картами пользователя с возможностью поиска.
     * Поддерживает поиск по последним 4 цифрам номера карты или имени владельца карты.
     *
     * @param user текущий пользователь
     * @param pageable параметры пагинации и сортировки
     * @param search поисковый запрос (последние 4 цифры или имя владельца)
     * @return страница с DTO карт пользователя
     */
    Page<CardDto> getUserCards(User user, Pageable pageable, String search);

    /**
     * Получает конкретную карту по идентификатору.
     * Проверяет, что карта принадлежит указанному пользователю.
     *
     * @param cardId идентификатор карты
     * @param user текущий пользователь
     * @return DTO запрошенной карты
     * @throws com.example.bankcards.exception.ResourceNotFoundException если карта не найдена или не принадлежит пользователю
     */
    CardDto getCardById(UUID cardId, User user);

    /**
     * Блокирует карту пользователя.
     * Не позволяет блокировать уже заблокированные или просроченные карты.
     *
     * @param cardId идентификатор карты для блокировки
     * @param user текущий пользователь
     * @return DTO заблокированной карты
     * @throws com.example.bankcards.exception.ResourceNotFoundException если карта не найдена или не принадлежит пользователю
     * @throws com.example.bankcards.exception.InvalidCardOperationException если карта уже заблокирована или просрочена
     */
    CardDto blockCard(UUID cardId, User user);

    /**
     * Активирует ранее заблокированную карту.
     * Не позволяет активировать уже активные или просроченные карты.
     *
     * @param cardId идентификатор карты для активации
     * @param user текущий пользователь
     * @return DTO активированной карты
     * @throws com.example.bankcards.exception.ResourceNotFoundException если карта не найдена или не принадлежит пользователю
     * @throws com.example.bankcards.exception.InvalidCardOperationException если карта уже активна или просрочена
     */
    CardDto activateCard(UUID cardId, User user);

    /**
     * Удаляет карту пользователя.
     * Проверяет, что карта принадлежит указанному пользователю.
     *
     * @param cardId идентификатор карты для удаления
     * @param user текущий пользователь
     * @throws com.example.bankcards.exception.ResourceNotFoundException если карта не найдена или не принадлежит пользователю
     */
    void deleteCard(UUID cardId, User user);

    /**
     * Получает баланс конкретной карты.
     * Проверяет, что карта принадлежит указанному пользователю.
     *
     * @param cardId идентификатор карты
     * @param user текущий пользователь
     * @return баланс карты
     * @throws com.example.bankcards.exception.ResourceNotFoundException если карта не найдена или не принадлежит пользователю
     */
    BigDecimal getCardBalance(UUID cardId, User user);

    /**
     * Получает агрегированную информацию о балансах всех карт пользователя.
     * Включает общий баланс, балансы по отдельным картам и статистику по картам.
     *
     * @param user текущий пользователь
     * @param pageable параметры пагинации для списка карт
     * @return ответ с информацией о балансах
     */
    BalanceResponse getUserBalance(User user, Pageable pageable);
}
