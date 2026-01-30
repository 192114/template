package com.shadow.template.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

@Validated
@ConfigurationProperties(prefix = "spring")
@Getter
@Setter
public class InfraProperties {

  @Valid
  private Datasource datasource = new Datasource();

  @Valid
  private Mail mail = new Mail();

  @Getter
  @Setter
  public static class Datasource {
    @NotBlank
    private String url;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String driverClassName;
  }

  @Getter
  @Setter
  public static class Mail {
    @NotBlank
    private String host;

    @Min(1)
    private int port;

    @NotBlank
    private String username;

    @NotBlank
    private String password;
  }
}