package com.example.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Benefícios API")
                        .description("API REST para gerenciamento de benefícios com transferência segura.")
                        .version("1.0.0")
                        .license(new License().name("MIT")));
    }
}
