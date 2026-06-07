package com.minimarket.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de OpenAPI / Swagger UI. Declara el esquema de seguridad
 * "bearer-jwt" para que, desde la interfaz de Swagger, se pueda autorizar con
 * el token obtenido en /api/auth/login y probar los endpoints protegidos.
 */
@Configuration
public class OpenApiConfig {

    private static final String SCHEME = "bearer-jwt";

    @Bean
    public OpenAPI miniMarketOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MiniMarket Plus API")
                        .version("1.0")
                        .description("API REST con seguridad Spring Security + JWT (Desarrollo Backend II)"))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME))
                .components(new Components().addSecuritySchemes(SCHEME,
                        new SecurityScheme()
                                .name(SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
