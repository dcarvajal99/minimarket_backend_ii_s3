package com.minimarket.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.minimarket.security.xss.XssStringDeserializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Personaliza Jackson:
 * <ul>
 *     <li>Registra el deserializador anti-XSS para String, de modo que toda
 *         cadena recibida en los cuerpos JSON se sanee con jsoup.</li>
 *     <li>Instala el modulo JavaTimeModule para serializar tipos de fecha/hora
 *         de Java 8 (LocalDateTime) usados en ApiResponse/ErrorResponse.</li>
 * </ul>
 * Se usa {@code modulesToInstall} para AGREGAR estos modulos sin reemplazar los
 * que Spring Boot registra automaticamente.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        SimpleModule xssModule = new SimpleModule();
        xssModule.addDeserializer(String.class, new XssStringDeserializer());
        return builder -> builder.modulesToInstall(xssModule, new JavaTimeModule());
    }
}
