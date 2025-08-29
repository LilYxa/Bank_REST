package com.example.bankcards.exception;

import com.example.bankcards.util.Constants;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super(Constants.EMAIL_STRING + " " + email + Constants.BUSY_EMAIL_MSG);
    }
}
