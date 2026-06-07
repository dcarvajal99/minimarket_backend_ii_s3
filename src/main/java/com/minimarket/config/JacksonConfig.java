package com.minimarket.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.minimarket.security.xss.XssStringDeserializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registra de forma global el deserializador anti-XSS para el tipo String, de
 * modo que toda cadena recibida en los cuerpos JSON sea saneada con jsoup
 * automaticamente antes de procesarse.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer xssJacksonCustomizer() {
        return builder -> {
            SimpleModule module = new SimpleModule();
            module.addDeserializer(String.class, new XssStringDeserializer());
            builder.modules(module);
        };
    }
}
