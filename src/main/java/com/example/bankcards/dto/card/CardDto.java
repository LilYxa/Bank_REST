package com.example.bankcards.dto.card;

import com.example.bankcards.entity.enums.CardStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardDto {

    private String cardNumber;

    private String lastFourDigits;

    private String cardOwner;

    private LocalDate expiryDate;

    private CardStatus status;

    private BigDecimal balance;
}
