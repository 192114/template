package com.shadow.template.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {

  @Valid private Jwt jwt = new Jwt();

  @Valid private Security security = new Security();

  @Valid private Refresh refresh = new Refresh();

  @Getter
  @Setter
  public static class Jwt {
    @NotBlank private String secret;

    @NotBlank private String issuer;

    @Min(60)
    private long expireSeconds;

    @AssertTrue(message = "JWT secret 长度至少 32 bytes")
    public boolean isSecretLengthValid() {
      return secret != null && secret.getBytes(StandardCharsets.UTF_8).length >= 32;
    }
  }

  @Getter
  @Setter
  public static class Refresh {
    @Min(1)
    private long expireDays;
  }

  @Getter
  @Setter
  public static class Security {
    @NotBlank private String permitAll;

    @Valid private Cors cors = new Cors();

    @Getter
    @Setter
    public static class Cors {
      @NotBlank private String allowedOrigins;

      @NotBlank private String allowedMethods;

      @NotBlank private String allowedHeaders;

      @NotNull private Boolean allowCredentials;
    }
  }
}
