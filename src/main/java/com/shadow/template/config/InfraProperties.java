package com.shadow.template.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "spring")
public class InfraProperties {

  @Valid
  private Datasource datasource = new Datasource();

  @Valid
  private Mail mail = new Mail();

  public Datasource getDatasource() {
    return datasource;
  }

  public void setDatasource(Datasource datasource) {
    this.datasource = datasource;
  }

  public Mail getMail() {
    return mail;
  }

  public void setMail(Mail mail) {
    this.mail = mail;
  }

  public static class Datasource {
    @NotBlank
    private String url;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String driverClassName;

    // getters/setters
  }

  public static class Mail {
    @NotBlank
    private String host;

    @Min(1)
    private int port;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    // getters/setters
  }
}