package com.techcup.techcup_futbol;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TechcupFutbolApplicationTests {

	@Test
	void contextLoads() {
	}
	@Test
	void generarHash() {
		var encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
		System.out.println("HASH: " + encoder.encode("Admin123"));
	}

	@Test
	void tokenExpiradoLanzaExcepcion() throws InterruptedException {
		var jwtUtil = new com.techcup.techcup_futbol.security.JwtUtil();
		org.springframework.test.util.ReflectionTestUtils.setField(
				jwtUtil, "secretKey",
				"TechCupFutbol2026ClaveSecretaMuyLargaParaQueSean256BitsMinimo!!");
		org.springframework.test.util.ReflectionTestUtils.setField(
				jwtUtil, "expirationMs", 1L);

		var user = new org.springframework.security.core.userdetails.User(
				"organizador@escuelaing.edu.co", "Admin123",
				java.util.List.of(new org.springframework.security.core.authority
						.SimpleGrantedAuthority("ROLE_CAPITAN")));

		String tokenExpirado = jwtUtil.generateToken(user);
		Thread.sleep(10);

		org.junit.jupiter.api.Assertions.assertThrows(
				io.jsonwebtoken.ExpiredJwtException.class,
				() -> jwtUtil.extractUsername(tokenExpirado),
				"Debe lanzar ExpiredJwtException"
		);
	}


}
