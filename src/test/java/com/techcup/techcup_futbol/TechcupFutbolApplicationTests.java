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

}
