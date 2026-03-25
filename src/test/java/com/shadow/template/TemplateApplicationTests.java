package com.shadow.template;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    properties = {
      "spring.flyway.enabled=false",
      "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1",
      "spring.datasource.driver-class-name=org.h2.Driver",
      "spring.datasource.username=sa",
      "spring.datasource.password=test",
      "spring.data.redis.host=localhost",
      "spring.data.redis.port=6379",
      "spring.data.redis.username=",
      "spring.data.redis.password=",
      "spring.mail.host=localhost",
      "spring.mail.port=25",
      "spring.mail.username=test@example.com",
      "spring.mail.password=test",
      "app.jwt.secret=12345678901234567890123456789012",
      "app.jwt.issuer=test",
      "app.jwt.expire-seconds=3600",
      "app.refresh.expire-days=7",
      "app.security.permit-all=/auth/**,/actuator/health,/actuator/health/**",
      "app.security.cors.allowed-origins=http://localhost:3000",
      "app.security.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS",
      "app.security.cors.allowed-headers=*",
      "app.security.cors.allow-credentials=true"
    })
class TemplateApplicationTests {

  @Test
  void contextLoads() {}
}
