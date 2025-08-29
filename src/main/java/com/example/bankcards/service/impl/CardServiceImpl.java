package com.example.bankcards.service.impl;

import com.example.bankcards.dto.card.BalanceResponse;
import com.example.bankcards.dto.card.CardCreateRequest;
import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.InvalidCardOperationException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.CardNumberUtils;
import com.example.bankcards.util.Constants;
import com.example.bankcards.util.mappers.CardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Реализация сервиса для управления банковскими картами.
 * Обеспечивает создание, управление и получение информации о картах пользователей.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final TextEncryptor textEncryptor;
    private final CardMapper cardMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public CardDto createCard(CardCreateRequest cardCreateRequest) {
        log.debug("createCard[1]: creating card: {}", cardCreateRequest);
        User cardOwner = userRepository.findById(cardCreateRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(Constants.USER_RESOURCE));
        String cardNumber = CardNumberUtils.generateCardNumber();

        String lastFourDigits = cardNumber.substring(cardNumber.length() - 4);
        log.debug("createCard[2]: last four digits: {}", lastFourDigits);
        String encryptedNumber = textEncryptor.encrypt(cardNumber);

        Card card = Card.builder()
                .cardOwner(cardCreateRequest.getCardOwner())
                .cardNumber(encryptedNumber)
                .lastFourDigits(lastFourDigits)
                .expiryDate(cardCreateRequest.getExpiryDate())
                .status(CardStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .user(cardOwner)
                .build();

        return cardMapper.cardToCardDto(cardRepository.save(card));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<CardDto> getUserCards(User user, Pageable pageable, String search) {
        log.debug("getUserCards[1]: Searching user cards");
        Page<Card> cards;

        if (search != null && !search.trim().isEmpty()) {
            String searchTerm = search.trim();
            log.debug("getUserCards[2]: Search term: {}", searchTerm);
            if (searchTerm.matches("\\d{4}")) {
                cards = cardRepository.findByUserAndLastFourDigits(user, searchTerm, pageable);
            } else {
                cards = cardRepository.findByUserAndCardOwnerContainingIgnoreCase(user, searchTerm, pageable);
            }
        } else {
            cards = cardRepository.findByUser(user, pageable);
        }
        log.debug("getUserCards[3]: cards: {}", cards);
        return cards.map(cardMapper::mapToCardResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardDto getCardById(UUID cardId, User user) {
        Card card = cardRepository.findByIdAndUser(cardId, user).orElseThrow(
                () -> new ResourceNotFoundException(Constants.CARD_RESOURCE));
        return cardMapper.mapToCardResponse(card);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public CardDto blockCard(UUID cardId, User user) {
        log.debug("blockCard[1]: Blocking card with id: {}", cardId);
        Card card = cardRepository.findByIdAndUser(cardId, user)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.CARD_RESOURCE));
        log.debug("blockCard[2]: Card: {}", card);

        if (card.getStatus().equals(CardStatus.BLOCKED)) {
            throw new InvalidCardOperationException(Constants.CARD_ALREADY_BLOCKED);
        }

        if (card.getStatus().equals(CardStatus.EXPIRED)) {
            throw new InvalidCardOperationException(Constants.CANNOT_BLOCK_EXPIRED_CARD);
        }

        card.setStatus(CardStatus.BLOCKED);
        return cardMapper.cardToCardDto(cardRepository.save(card));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public CardDto activateCard(UUID cardId, User user) {
        log.debug("activateCard[1]: Activating card with id: {}", cardId);
        Card card = cardRepository.findByIdAndUser(cardId, user)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.CARD_RESOURCE));
        log.debug("activateCard[2]: Card: {}", card);

        if (card.getStatus() == CardStatus.ACTIVE) {
            throw new InvalidCardOperationException(Constants.CARD_ALREADY_ACTIVE);
        }

        if (card.getStatus() == CardStatus.EXPIRED) {
            throw new InvalidCardOperationException(Constants.CANNOT_ACTIVE_EXPIRED_CARD);
        }

        card.setStatus(CardStatus.ACTIVE);
        return cardMapper.cardToCardDto(cardRepository.save(card));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteCard(UUID cardId, User user) {
        log.debug("deleteCard[1]: Delete card with id: {}", cardId);
        Card card = cardRepository.findByIdAndUser(cardId, user)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.CARD_RESOURCE));

        cardRepository.delete(card);
    }

    private String getDecryptedCardNumber(UUID cardId) {
        Optional<Card> cardOptional = cardRepository.findById(cardId);
        if (cardOptional.isPresent()) {
            Card card = cardOptional.get();
            return textEncryptor.decrypt(card.getCardNumber());
        }
        throw new ResourceNotFoundException(Constants.CARD_RESOURCE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public BigDecimal getCardBalance(UUID cardId, User user) {
        log.debug("getCardBalance[1]: Getting balance for card: {}, user: {}", cardId, user.getEmail());

        Card card = cardRepository.findByIdAndUser(cardId, user)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.CARD_RESOURCE));

        log.debug("getCardBalance[2]: Card balance: {}", card.getBalance());
        return card.getBalance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public BalanceResponse getUserBalance(User user, Pageable pageable) {
        log.debug("getUserBalance[1]: Getting balance for user: {}", user.getEmail());

        Page<Card> userCards = cardRepository.findByUser(user, pageable);

        BigDecimal totalBalance = BigDecimal.ZERO;
        Map<UUID, BigDecimal> cardBalances = new HashMap<>();
        int activeCardsCount = 0;

        for (Card card : userCards) {
            cardBalances.put(card.getId(), card.getBalance());
            totalBalance = totalBalance.add(card.getBalance());

            if (card.getStatus() == CardStatus.ACTIVE) {
                activeCardsCount++;
            }
        }

        BalanceResponse response = BalanceResponse.builder()
                .totalBalance(totalBalance)
                .cardBalances(cardBalances)
                .totalCards((int) userCards.getTotalElements())
                .activeCards(activeCardsCount)
                .build();

        log.debug("getUserBalance[2]: Balance response: {}", response);
        return response;
    }
}
