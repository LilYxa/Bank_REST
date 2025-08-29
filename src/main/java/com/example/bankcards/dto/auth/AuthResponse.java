package com.example.bankcards.dto.auth;

import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {

    private String accessToken;

    private String tokenType = Constants.BEARER_TOKEN;

    private UserDto user;
}
