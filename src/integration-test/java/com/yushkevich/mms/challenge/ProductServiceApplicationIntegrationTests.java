package com.yushkevich.mms.challenge;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test-containers-flyway")
class ProductServiceApplicationIntegrationTests {

  @Test
  void contextLoads() {}
}
