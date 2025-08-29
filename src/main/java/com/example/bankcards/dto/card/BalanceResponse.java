package com.example.bankcards.dto.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BalanceResponse {
    private BigDecimal totalBalance;
    private Map<UUID, BigDecimal> cardBalances;
    private int totalCards;
    private int activeCards;
}
