package com.example.bankcards.service.impl;

import com.example.bankcards.dto.auth.RegisterRequest;
import com.example.bankcards.dto.card.BalanceUpdateRequest;
import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.dto.user.UserUpdateRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.exception.EmailAlreadyExistsException;
import com.example.bankcards.exception.InvalidCardOperationException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.exception.UserAlreadyExistsException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.AdminService;
import com.example.bankcards.util.Constants;
import com.example.bankcards.util.mappers.CardMapper;
import com.example.bankcards.util.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Реализация аервиса для административных операций управления пользователями и картами.
 * Предоставляет методы для работы с пользовательскими аккаунтами и банковскими картами
 * с правами администратора системы.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminServiceImpl implements AdminService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CardMapper cardMapper;
    private final UserMapper userMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CardDto> getAllCards(Pageable pageable) {
        log.debug("getAllCards[1]: pageable={}", pageable);

        return cardRepository.findAll(pageable)
                .map(cardMapper::mapToCardResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardDto updateCardBalance(BalanceUpdateRequest request) {
        log.debug("updateCardBalance[1]: Updating balance for card: {}", request.getCardId());

        Card card = cardRepository.findById(request.getCardId())
                .orElseThrow(() -> new ResourceNotFoundException(Constants.CARD_RESOURCE));

        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new InvalidCardOperationException(Constants.CANNOT_UPDATE_INACTIVE_CARD_BALANCE);
        }

        card.setBalance(request.getAmount());

        Card updatedCard = cardRepository.save(card);

        log.info("updateCardBalance[1]: Balance updated for card {}: new balance = {}",
                request.getCardId(), request.getAmount());

        return cardMapper.mapToCardResponse(updatedCard);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDto createUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(Constants.USER_ALREADY_EXISTS);
        }

        var user = User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .patronymic(request.getPatronymic())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .enabled(true)
                .accountNonLocked(true)
                .build();

        return userMapper.userToUserDto(userRepository.save(user));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        log.debug("getAllUsers: pageable={}", pageable);

        return userRepository.findAll(pageable)
                .map(userMapper::userToUserDto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.USER_RESOURCE));
        return userMapper.userToUserDto(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDto updateUserStatus(UUID userId, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.USER_RESOURCE));

        user.setEnabled(enabled);
        user = userRepository.save(user);

        return userMapper.userToUserDto(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDto updateUserLockStatus(UUID userId, boolean accountNonLocked) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.USER_RESOURCE));

        user.setAccountNonLocked(accountNonLocked);
        user = userRepository.save(user);

        return userMapper.userToUserDto(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDto updateUser(UUID userId, UserUpdateRequest request) {
        log.debug("updateUser[1]: userId={}, request={}", userId, request);

        User existedUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.USER_RESOURCE));

        String currentPassword = existedUser.getPassword();

        if (request.getFirstName() != null) {
            existedUser.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            existedUser.setLastName(request.getLastName());
        }
        if (request.getPatronymic() != null) {
            existedUser.setPatronymic(request.getPatronymic());
        }
        if (request.getEmail() != null && !request.getEmail().equals(existedUser.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new UserAlreadyExistsException(Constants.BUSY_EMAIL_MSG);
            }
            existedUser.setEmail(request.getEmail());
        }
        if (request.getRole() != null) {
            existedUser.setRole(request.getRole());
        }

        existedUser.setPassword(currentPassword);

        User updatedUser = userRepository.save(existedUser);
        return userMapper.userToUserDto(updatedUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteUser(UUID userId) {
        log.debug("deleteUser[1]: user ID {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.USER_RESOURCE));

        userRepository.delete(user);
    }
}
