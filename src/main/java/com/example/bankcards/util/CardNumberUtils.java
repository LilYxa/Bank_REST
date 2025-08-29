package com.example.bankcards.util;


import com.example.bankcards.entity.Card;

import java.util.Random;

public class CardNumberUtils {

    private static final Random random = new Random();

    public static String generateCardNumber() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < Constants.CARD_NUMBER_LENGTH; i++) {
            stringBuilder.append(random.nextInt(10));
        }

        return stringBuilder.toString();
    }

    public static String generateCardNumber(String bin) {
        StringBuilder stringBuilder = new StringBuilder();

        if (bin != null && bin.matches("\\d{6}")) {
            stringBuilder.append(bin);

            while (stringBuilder.length() < Constants.CARD_NUMBER_LENGTH) {
                stringBuilder.append(random.nextInt(10));
            }
        } else {
            return generateCardNumber();
        }
        return stringBuilder.toString();
    }

    public static boolean validateCardNumber(String cardNumber) {
        return cardNumber != null && cardNumber.matches("\\d{16}");
    }

    public static String getMaskedCardNumber(Card card) {
        return "**** **** **** " + card.getLastFourDigits();
    }
}
