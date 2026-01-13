package com.brajesh.moneymgmt;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MoneymgmtApplication.class)
@ActiveProfiles("test")
class MoneymgmtApplicationTests {

	@Test
	void contextLoads() {
		// Sanity test - just loads Spring context
	}
}
