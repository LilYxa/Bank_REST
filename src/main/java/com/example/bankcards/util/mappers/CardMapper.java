package com.example.bankcards.util.mappers;

import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.util.Constants;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CardMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    CardDto cardToCardDto(Card card);

    Card cardDtoToCard(CardDto cardDto);

    default CardDto mapToCardResponse(Card card) {
        return CardDto.builder()
                .cardNumber(Constants.CARD_MASK + card.getLastFourDigits())
                .lastFourDigits(card.getLastFourDigits())
                .cardOwner(card.getCardOwner())
                .expiryDate(card.getExpiryDate())
                .status(card.getStatus())
                .balance(card.getBalance())
                .build();
    }
}
