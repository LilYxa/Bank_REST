package com.example.bankcards.controller;

import com.example.bankcards.dto.card.BalanceResponse;
import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.card.TransferRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.TransactionService;
import com.example.bankcards.util.Constants;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user/cards")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class UserController {

    private final CardService cardService;
    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<Page<CardDto>> getUserCards(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<CardDto> userCards = cardService.getUserCards(user, pageable, search);
        return ResponseEntity.ok(userCards);
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CardDto> getCardDetails(@PathVariable UUID cardId, @AuthenticationPrincipal User user) {
        CardDto cardDetail = cardService.getCardById(cardId, user);
        return ResponseEntity.ok(cardDetail);
    }

    @PostMapping("/{cardId}/block")
    public ResponseEntity<CardDto> blockCard(@PathVariable UUID cardId, @AuthenticationPrincipal User user) {
        CardDto blockedCard = cardService.blockCard(cardId, user);
        return ResponseEntity.ok(blockedCard);
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferBetweenCards(
            @RequestBody TransferRequest request,
            @AuthenticationPrincipal User user) {
        transactionService.transferBetweenOwnCards(
                request.getFromCardId(),
                request.getToCardId(),
                request.getAmount(),
                user
        );
        return ResponseEntity.ok(Constants.SUCCESSFUL_TRANSFER);
    }

    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> getUserBalance(
            @AuthenticationPrincipal User user,
            @PageableDefault() Pageable pageable) {

        BalanceResponse balance = cardService.getUserBalance(user, pageable);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/card/{cardId}/balance")
    public ResponseEntity<BigDecimal> getCardBalance(
            @PathVariable UUID cardId,
            @AuthenticationPrincipal User user) {

        BigDecimal balance = cardService.getCardBalance(cardId, user);
        return ResponseEntity.ok(balance);
    }
}
