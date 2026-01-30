package com.shadow.template.config;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {

  @Valid
  private Jwt jwt = new Jwt();

  @Valid
  private Security security = new Security();

  @Getter
  @Setter
  public static class Jwt {
    @NotBlank
    private String secret;

    @NotBlank
    private String issuer;

    @Min(60)
    private long expireSeconds;

    @NotBlank
    private String refreshSecret;

    @Min(60)
    private long refreshExpireSeconds;

    @PostConstruct
    public void validate() {
      if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
        throw new IllegalStateException("JWT secret 长度至少 32 bytes");
      }
      if (refreshSecret.getBytes(StandardCharsets.UTF_8).length < 32) {
        throw new IllegalStateException("JWT refresh secret 长度至少 32 bytes");
      }
    }
  }

  @Getter
  @Setter
  public static class Security {
    @NotBlank
    private String permitAll;

    @Valid
    private Cors cors = new Cors();
    
    @Getter
    @Setter
    public static class Cors {
      @NotBlank
      private String allowedOrigins;

      @NotBlank
      private String allowedMethods;

      @NotBlank
      private String allowedHeaders;

      @NotNull
      private Boolean allowCredentials;
    }
  }
}