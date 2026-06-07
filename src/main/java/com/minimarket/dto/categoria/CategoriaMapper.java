package com.minimarket.dto.categoria;

import com.minimarket.entity.Categoria;

/**
 * Mapper entre la entidad Categoria y sus DTOs. Centraliza la conversion para
 * mantener los controladores limpios y desacoplados de la entidad JPA.
 */
public final class CategoriaMapper {

    private CategoriaMapper() {
    }

    /** Convierte la entidad a su DTO de salida. */
    public static CategoriaResponse toResponse(Categoria categoria) {
        CategoriaResponse dto = new CategoriaResponse();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        dto.setCantidadProductos(categoria.getProductos() != null ? categoria.getProductos().size() : 0);
        return dto;
    }

    /** Construye una entidad Categoria a partir del DTO de entrada. */
    public static Categoria toEntity(CategoriaRequest req) {
        Categoria categoria = new Categoria();
        categoria.setNombre(req.getNombre());
        return categoria;
    }
}
