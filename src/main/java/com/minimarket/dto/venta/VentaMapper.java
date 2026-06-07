package com.minimarket.dto.venta;

import com.minimarket.entity.Usuario;
import com.minimarket.entity.Venta;

import java.util.Date;

/**
 * Mapper entre la entidad Venta y sus DTOs. Centraliza la conversion para
 * mantener los controladores limpios y desacoplados de la entidad JPA.
 */
public final class VentaMapper {

    private VentaMapper() {
    }

    /** Convierte la entidad a su DTO de salida. */
    public static VentaResponse toResponse(Venta venta) {
        VentaResponse dto = new VentaResponse();
        dto.setId(venta.getId());
        dto.setFecha(venta.getFecha());
        if (venta.getUsuario() != null) {
            dto.setUsuarioId(venta.getUsuario().getId());
            dto.setUsuarioUsername(venta.getUsuario().getUsername());
        }
        dto.setCantidadDetalles(venta.getDetalles() != null ? venta.getDetalles().size() : 0);
        return dto;
    }

    /**
     * Construye una entidad Venta a partir del DTO de entrada y el usuario ya
     * resuelto por el servicio. La fecha se asigna en el servidor.
     */
    public static Venta toEntity(VentaRequest req, Usuario usuario) {
        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setFecha(new Date());
        return venta;
    }
}
