package com.example.bankcards.service;

import com.example.bankcards.entity.User;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Сервис для выполнения финансовых операций и транзакций.
 * Предоставляет методы для управления денежными переводами между картами.
 */
public interface TransactionService {

    /**
     * Выполняет перевод денежных средств между картами одного пользователя.
     * Проверяет возможность перевода, включая статус карт, достаточность средств и валидность суммы.
     *
     * @param fromCardId идентификатор карты-отправителя
     * @param toCardId идентификатор карты-получателя
     * @param amount сумма перевода
     * @param user пользователь, выполняющий операцию
     * @throws com.example.bankcards.exception.ResourceNotFoundException если одна из карт не найдена или не принадлежит пользователю
     * @throws com.example.bankcards.exception.InvalidCardOperationException если:
     *         - карта-отправитель не активна
     *         - карта-получатель не активна
     *         - сумма перевода отрицательна или равна нулю
     *         - недостаточно средств на карте-отправителе
     *         - попытка перевода на ту же карту
     */
    void transferBetweenOwnCards(UUID fromCardId, UUID toCardId, BigDecimal amount, User user);
}
