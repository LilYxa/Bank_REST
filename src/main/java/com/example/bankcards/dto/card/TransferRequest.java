package com.example.bankcards.dto.card;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

    @NotNull(message = "Source card ID is required")
    private UUID fromCardId;

    @NotNull(message = "Destination card ID is required")
    private UUID toCardId;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;
}
