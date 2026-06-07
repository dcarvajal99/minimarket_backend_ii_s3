package com.minimarket.dto.venta;

import java.util.Date;

/**
 * DTO de salida de venta. Aplana el usuario a id + username y resume los
 * detalles a su cantidad para no exponer la entidad JPA ni provocar recursion.
 */
public class VentaResponse {

    private Long id;
    private Date fecha;
    private Long usuarioId;
    private String usuarioUsername;
    private int cantidadDetalles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioUsername() {
        return usuarioUsername;
    }

    public void setUsuarioUsername(String usuarioUsername) {
        this.usuarioUsername = usuarioUsername;
    }

    public int getCantidadDetalles() {
        return cantidadDetalles;
    }

    public void setCantidadDetalles(int cantidadDetalles) {
        this.cantidadDetalles = cantidadDetalles;
    }
}
