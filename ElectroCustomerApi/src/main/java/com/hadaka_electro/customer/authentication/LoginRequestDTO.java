package com.hadaka_electro.customer.authentication;

public record LoginRequestDTO(
        String email,
        String password,
        boolean rememberMe
) {
}
