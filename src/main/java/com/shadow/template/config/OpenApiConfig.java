package com.shadow.template.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Template API")
            .description("Spring Boot 模板工程 REST API 文档")
            .version("0.0.1"));
  }
}