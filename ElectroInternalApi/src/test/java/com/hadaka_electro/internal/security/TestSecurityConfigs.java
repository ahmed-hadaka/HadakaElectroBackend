package com.hadaka_electro.internal.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestSecurityConfigs {

	@Test
	public void testPasswordEncoder() {
		BCryptPasswordEncoder pBCryptPasswordEncoder = new BCryptPasswordEncoder();
		String rawPassword = "123#Ahmed";
		String encodedPassword = pBCryptPasswordEncoder.encode(rawPassword);

		System.out.println(encodedPassword);

		assertEquals(true, pBCryptPasswordEncoder.matches(rawPassword, encodedPassword));
	}

}
