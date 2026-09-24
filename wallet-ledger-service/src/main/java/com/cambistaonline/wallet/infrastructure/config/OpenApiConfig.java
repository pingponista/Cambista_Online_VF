package com.cambistaonline.wallet.infrastructure.config;

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
                        .title("Cambista Online Wallet & Ledger Service API")
                        .description("Servicio central de saldos multimoneda (PEN, USD, EUR) y libro mayor contable (Double-Entry Ledger).")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Fintech Ledger Team")
                                .email("ledger@cambistaonline.pe")));
    }
}
