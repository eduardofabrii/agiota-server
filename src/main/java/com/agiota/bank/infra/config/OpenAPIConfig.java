package com.agiota.bank.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Agiota API Documentation")
                        .version("1.0")
                        .description("Documentação da API do Agiota Server para o projeto de Back-end: Web Services\n\n" +
                                "**AVISO: Para acessar os endpoints protegidos desta API, é necessário gerar um token JWT " +
                                "através do endpoint de autenticação e incluí-lo no campo de autenticação 'Bearer' acima.**"))

                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .name("Bearer Authentication")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("É necessário gerar um token JWT através do endpoint de autenticação e incluí-lo aqui. Formato: Bearer {token}")
                        )
                );
    }
}
