package com.minimarket.dto.categoria;

/**
 * DTO de salida de categoria. Expone el conteo de productos en lugar de la
 * lista completa para no exponer la entidad JPA ni provocar recursion en la
 * serializacion.
 */
public class CategoriaResponse {

    private Long id;
    private String nombre;
    private int cantidadProductos;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidadProductos() {
        return cantidadProductos;
    }

    public void setCantidadProductos(int cantidadProductos) {
        this.cantidadProductos = cantidadProductos;
    }
}
