package com.example.bankcards.util;

public class Constants {

    public static final String ACCESS_TOKEN_TYPE = "Access";
    public static final String REFRESH_TOKEN_TYPE = "Refresh";
    public static final String BEARER_TOKEN = "Bearer";
    public static final String REFRESH_TOKEN = "refreshToken";

    public static final String USERNAME_NOT_FOUND = "User not found: %s";
    public static final String UNAUTHORIZED_ERROR_MESSAGE = "Sorry, You're not authorized to access this resource.";
    public static final String RESOURCE_NOT_EXIST = "Resource %s not found";
    public static final String USER_ALREADY_EXISTS = "User with this email already exists";
    public static final String REFRESH_TOKEN_MISSING = "Refresh token missing";
    public static final String INVALID_REFRESH_TOKEN_MSG = "Refresh token invalid";
    public static final String CARD_ALREADY_BLOCKED = "Card is already blocked";
    public static final String CANNOT_BLOCK_EXPIRED_CARD = "Cannot block expired card";
    public static final String CARD_ALREADY_ACTIVE = "Card is already active";
    public static final String CANNOT_ACTIVE_EXPIRED_CARD = "Cannot activate expired card";
    public static final String SOURCE_CARD_NOT_ACTIVE = "Source card is not active";
    public static final String DESTINATION_CARD_NOT_ACTIVE = "Destination card is not active";
    public static final String NEGATIVE_AMOUNT = "Amount must be positive";
    public static final String INSUFFICIENT_FUNDS = "Insufficient funds";
    public static final String SAME_CARD_TRANSFER_MSG = "Cannot transfer to the same card";
    public static final String SUCCESSFUL_TRANSFER = "The transfer was completed successfully";
    public static final String BUSY_EMAIL_MSG = "already is busy";
    public static final String CANNOT_UPDATE_INACTIVE_CARD_BALANCE = "You cannot change the balance of an inactive card";

    public static final String CARD_DELETED_MSG = "Card was successfully deleted!";

    public static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    public static final String USER_RESOURCE = "User";
    public static final String CARD_RESOURCE = "Card";
    public static final String EMAIL_STRING = "Email";

    public static final String ROLE_CLAIM = "role";

    public static final String COOKIE_SET_COOKIE_HEADER = "Set-Cookie";
    public static final String COOKIE_MAKE_HEADERS = "refreshToken=%s; HttpOnly; Max-Age=%d; Path=/; SameSite=Lax; Secure";
    public static final String COOKIE_CLEAR_HEADERS = "refreshToken=; HttpOnly; Max-Age=0; Path=/; SameSite=Lax; Secure";

    public static final String DOTENV_PROPS = "dotenvProperties";

    public static final int CARD_NUMBER_LENGTH = 16;
    public static final String CARD_MASK = "**** **** **** ";
}
