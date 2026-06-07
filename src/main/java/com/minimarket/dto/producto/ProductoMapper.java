package com.minimarket.dto.producto;

import com.minimarket.entity.Categoria;
import com.minimarket.entity.Producto;

/**
 * Mapper entre la entidad Producto y sus DTOs. Centraliza la conversion para
 * mantener los controladores limpios y desacoplados de la entidad JPA.
 */
public final class ProductoMapper {

    private ProductoMapper() {
    }

    /** Convierte la entidad a su DTO de salida. */
    public static ProductoResponse toResponse(Producto producto) {
        ProductoResponse dto = new ProductoResponse();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());
        if (producto.getCategoria() != null) {
            dto.setCategoriaId(producto.getCategoria().getId());
            dto.setCategoriaNombre(producto.getCategoria().getNombre());
        }
        return dto;
    }

    /**
     * Construye una entidad Producto a partir del DTO de entrada y la categoria
     * ya resuelta por el servicio.
     */
    public static Producto toEntity(ProductoRequest req, Categoria categoria) {
        Producto producto = new Producto();
        producto.setNombre(req.getNombre());
        producto.setPrecio(req.getPrecio());
        producto.setStock(req.getStock());
        producto.setCategoria(categoria);
        return producto;
    }
}
