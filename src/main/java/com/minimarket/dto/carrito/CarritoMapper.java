package com.minimarket.dto.carrito;

import com.minimarket.entity.Carrito;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Usuario;

/**
 * Mapper entre la entidad Carrito y sus DTOs. Centraliza la conversion para
 * mantener los controladores limpios y desacoplados de la entidad JPA.
 */
public final class CarritoMapper {

    private CarritoMapper() {
    }

    /** Convierte la entidad a su DTO de salida, aplanando usuario y producto. */
    public static CarritoResponse toResponse(Carrito carrito) {
        CarritoResponse dto = new CarritoResponse();
        dto.setId(carrito.getId());
        dto.setCantidad(carrito.getCantidad());
        if (carrito.getUsuario() != null) {
            dto.setUsuarioId(carrito.getUsuario().getId());
            dto.setUsuarioUsername(carrito.getUsuario().getUsername());
        }
        if (carrito.getProducto() != null) {
            dto.setProductoId(carrito.getProducto().getId());
            dto.setProductoNombre(carrito.getProducto().getNombre());
        }
        return dto;
    }

    /**
     * Construye una entidad Carrito a partir del DTO de entrada y el usuario y
     * producto ya resueltos por el controlador.
     */
    public static Carrito toEntity(CarritoRequest req, Usuario usuario, Producto producto) {
        Carrito carrito = new Carrito();
        carrito.setUsuario(usuario);
        carrito.setProducto(producto);
        carrito.setCantidad(req.getCantidad());
        return carrito;
    }
}
