package com.hadaka_electro.internal.authentication;

public record LoginRequestDTO(
        String email,
        String password,
        boolean rememberMe
) {
}
