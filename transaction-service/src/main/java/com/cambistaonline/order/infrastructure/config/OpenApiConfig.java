package com.cambistaonline.order.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cambista Online Transaction Service API")
                        .description("Servicio central de creación, orquestación y confirmación de órdenes de cambio de divisas.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Fintech Transaction Team")
                                .email("transactions@cambistaonline.pe")));
    }
}
