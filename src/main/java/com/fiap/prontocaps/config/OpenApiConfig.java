package com.fiap.prontocaps.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("ProntoCAPS API")
            .description("API de prontuarios eletronicos para CAPS (SUS)")
            .version("1.0.0"));
  }
}
