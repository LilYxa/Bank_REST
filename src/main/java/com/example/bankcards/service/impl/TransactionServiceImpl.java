package com.example.bankcards.service.impl;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.InvalidCardOperationException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.TransactionService;
import com.example.bankcards.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Реализация сервиса для выполнения финансовых транзакций.
 * Обеспечивает безопасное выполнение переводов между картами с проверкой валидности операций.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final CardRepository cardRepository;
    private final TextEncryptor textEncryptor;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void transferBetweenOwnCards(UUID fromCardId, UUID toCardId, BigDecimal amount, User user) {
        log.debug("transferBetweenOwnCards[1]: Transfer from card with id: {} to card with id: {}", fromCardId, toCardId);

        Card fromCard = cardRepository.findByIdAndUser(fromCardId, user)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.CARD_RESOURCE));

        Card toCard = cardRepository.findByIdAndUser(toCardId, user)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.CARD_RESOURCE));

        validateTransfer(fromCard, toCard, amount);

        fromCard.setBalance(fromCard.getBalance().subtract(amount));
        toCard.setBalance(toCard.getBalance().add(amount));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        log.info("transferBetweenOwnCards[1]: Transfer completed: {} from card {} to card {}", amount, fromCardId, toCardId);
    }

    private void validateTransfer(Card fromCard, Card toCard, BigDecimal amount) {
        if (fromCard.getStatus() != CardStatus.ACTIVE) {
            throw new InvalidCardOperationException(Constants.SOURCE_CARD_NOT_ACTIVE);
        }

        if (toCard.getStatus() != CardStatus.ACTIVE) {
            throw new InvalidCardOperationException(Constants.DESTINATION_CARD_NOT_ACTIVE);
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidCardOperationException(Constants.NEGATIVE_AMOUNT);
        }

        if (fromCard.getBalance().compareTo(amount) < 0) {
            throw new InvalidCardOperationException(Constants.INSUFFICIENT_FUNDS);
        }

        if (fromCard.getId().equals(toCard.getId())) {
            throw new InvalidCardOperationException(Constants.SAME_CARD_TRANSFER_MSG);
        }
    }
}
