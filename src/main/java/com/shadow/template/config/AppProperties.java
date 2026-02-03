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

  @Valid
  private Refresh refresh = new Refresh();

  @Getter
  @Setter
  public static class Jwt {
    @NotBlank
    private String secret;

    @NotBlank
    private String issuer;

    @Min(60)
    private long expireSeconds;

    @PostConstruct
    public void validate() {
      if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
        throw new IllegalStateException("JWT secret 长度至少 32 bytes");
      }
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