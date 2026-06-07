package com.minimarket.security.xss;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Deserializador de Jackson que sanea contra XSS todas las cadenas entrantes
 * en los cuerpos JSON de las peticiones REST.
 *
 * <p>Se registra de forma global (ver {@code JacksonConfig}), por lo que cada
 * String recibido en un {@code @RequestBody} pasa por {@link XssSanitizer}
 * antes de llegar a la capa de servicio. De esta manera, la proteccion XSS se
 * aplica de manera transversal y automatica, sin depender de que cada
 * controlador la invoque manualmente.
 */
public class XssStringDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return XssSanitizer.clean(p.getValueAsString());
    }
}
