package com.example.bankcards.dto.card;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardCreateRequest {

    @NotBlank(message = "Card owner is required")
    @Size(max = 100, message = "Card owner must be less than 100 characters")
    private String cardOwner;

    @NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;

    @NotNull(message = "User id is required")
    private UUID userId;

    @Pattern(regexp = "^[0-9]{6}$", message = "BIN must be 6 digits")
    private String bin;
}
