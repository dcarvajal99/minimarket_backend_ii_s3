package com.minimarket.dto.detalleventa;

import com.minimarket.entity.DetalleVenta;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Venta;

/**
 * Mapper entre la entidad DetalleVenta y sus DTOs. Centraliza la conversion
 * para mantener los controladores limpios y desacoplados de la entidad JPA.
 */
public final class DetalleVentaMapper {

    private DetalleVentaMapper() {
    }

    /** Convierte la entidad a su DTO de salida. */
    public static DetalleVentaResponse toResponse(DetalleVenta detalle) {
        DetalleVentaResponse dto = new DetalleVentaResponse();
        dto.setId(detalle.getId());
        if (detalle.getVenta() != null) {
            dto.setVentaId(detalle.getVenta().getId());
        }
        if (detalle.getProducto() != null) {
            dto.setProductoId(detalle.getProducto().getId());
            dto.setProductoNombre(detalle.getProducto().getNombre());
        }
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecio(detalle.getPrecio());
        return dto;
    }

    /**
     * Construye una entidad DetalleVenta a partir del DTO de entrada y la venta
     * y el producto ya resueltos por el servicio.
     */
    public static DetalleVenta toEntity(DetalleVentaRequest req, Venta venta, Producto producto) {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setVenta(venta);
        detalle.setProducto(producto);
        detalle.setCantidad(req.getCantidad());
        detalle.setPrecio(req.getPrecio());
        return detalle;
    }
}
