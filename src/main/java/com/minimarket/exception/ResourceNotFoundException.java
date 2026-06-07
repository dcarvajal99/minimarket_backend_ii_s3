package com.minimarket.exception;

/**
 * Excepcion de dominio que indica que un recurso solicitado no existe.
 * El manejador global la traduce a una respuesta HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String recurso, Long id) {
        super(recurso + " no encontrado con id " + id);
    }
}
