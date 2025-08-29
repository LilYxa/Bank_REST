package com.example.bankcards.controller;

import com.example.bankcards.dto.auth.RegisterRequest;
import com.example.bankcards.dto.card.BalanceUpdateRequest;
import com.example.bankcards.dto.card.CardCreateRequest;
import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.dto.user.UserUpdateRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.AdminService;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class AdminController {

    private final AdminService adminService;
    private final CardService cardService;

    @GetMapping("/cards")
    public ResponseEntity<Page<CardDto>> getAllCards(
            @PageableDefault(size = 20, direction = Sort.Direction.DESC) Pageable pageable) {

        Page<CardDto> cards = adminService.getAllCards(pageable);
        return ResponseEntity.ok(cards);
    }

    @PatchMapping("/cards/balance")
    @Operation(summary = "Update card balance")
    public ResponseEntity<CardDto> updateCardBalance(
            @Valid @RequestBody BalanceUpdateRequest request) {

        CardDto card = adminService.updateCardBalance(request);
        return ResponseEntity.ok(card);
    }

    @GetMapping("/cards/{cardId}")
    @Operation(summary = "Get card by ID")
    public ResponseEntity<CardDto> getCard(@PathVariable UUID cardId,
                                           @AuthenticationPrincipal User user) {
        CardDto card = cardService.getCardById(cardId, user);
        return ResponseEntity.ok(card);
    }

    @PostMapping("/cards/create")
    @Operation(summary = "Create new card (Admin only)")
    public ResponseEntity<CardDto> createCard(@Valid @RequestBody CardCreateRequest request) {
        CardDto cardDto = cardService.createCard(request);
        return ResponseEntity.ok(cardDto);
    }

    @PostMapping("/cards/{cardId}/block")
    @Operation(summary = "Block card")
    public ResponseEntity<CardDto> blockCard(@PathVariable UUID cardId,
                                             @AuthenticationPrincipal User user) {
        CardDto cardDto = cardService.blockCard(cardId, user);
        return ResponseEntity.ok(cardDto);
    }

    @PostMapping("/cards/{cardId}/activate")
    @Operation(summary = "Activate card")
    public ResponseEntity<CardDto> activateCard(@PathVariable UUID cardId,
                                                @AuthenticationPrincipal User user) {
        CardDto cardDto = cardService.activateCard(cardId, user);
        return ResponseEntity.ok(cardDto);
    }

    @DeleteMapping("/cards/{cardId}/delete")
    @Operation(summary = "Delete card")
    public ResponseEntity<String> deleteCard(@PathVariable UUID cardId,
                                             @AuthenticationPrincipal User user) {
        cardService.deleteCard(cardId, user);
        return ResponseEntity.ok(Constants.CARD_DELETED_MSG);
    }

    @PostMapping("/users/create")
    public ResponseEntity<UserDto> createUser(@RequestBody RegisterRequest registerRequest) {
        UserDto userDto = adminService.createUser(registerRequest);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable) {

        Page<UserDto> users = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable UUID userId) {
        UserDto user = adminService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<UserDto> updateUserStatus(
            @PathVariable UUID userId,
            @RequestParam boolean enabled) {

        UserDto user = adminService.updateUserStatus(userId, enabled);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/users/{userId}/lock-status")
    public ResponseEntity<UserDto> updateUserLockStatus(
            @PathVariable UUID userId,
            @RequestParam boolean accountNonLocked) {

        UserDto user = adminService.updateUserLockStatus(userId, accountNonLocked);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UserUpdateRequest request) {

        UserDto user = adminService.updateUser(userId, request);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
