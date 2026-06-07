package com.minimarket.dto.inventario;

import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;

import java.util.Date;

/**
 * Mapper entre la entidad Inventario y sus DTOs. Centraliza la conversion para
 * mantener los controladores limpios y desacoplados de la entidad JPA.
 */
public final class InventarioMapper {

    private InventarioMapper() {
    }

    /** Convierte la entidad a su DTO de salida. */
    public static InventarioResponse toResponse(Inventario inventario) {
        InventarioResponse dto = new InventarioResponse();
        dto.setId(inventario.getId());
        dto.setCantidad(inventario.getCantidad());
        dto.setTipoMovimiento(inventario.getTipoMovimiento());
        dto.setFechaMovimiento(inventario.getFechaMovimiento());
        if (inventario.getProducto() != null) {
            dto.setProductoId(inventario.getProducto().getId());
            dto.setProductoNombre(inventario.getProducto().getNombre());
        }
        return dto;
    }

    /**
     * Construye una entidad Inventario a partir del DTO de entrada y el producto
     * ya resuelto por el servicio. La fecha del movimiento se establece en el
     * servidor.
     */
    public static Inventario toEntity(InventarioRequest req, Producto producto) {
        Inventario inventario = new Inventario();
        inventario.setCantidad(req.getCantidad());
        inventario.setTipoMovimiento(req.getTipoMovimiento());
        inventario.setProducto(producto);
        inventario.setFechaMovimiento(new Date());
        return inventario;
    }
}
