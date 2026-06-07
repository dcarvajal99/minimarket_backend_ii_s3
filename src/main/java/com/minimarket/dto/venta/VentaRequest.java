package com.minimarket.dto.venta;

import jakarta.validation.constraints.NotNull;

/**
 * DTO de entrada para registrar una venta. Solo expone el usuario asociado; la
 * fecha se asigna en el servidor y los detalles se gestionan por separado.
 */
public class VentaRequest {

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}
