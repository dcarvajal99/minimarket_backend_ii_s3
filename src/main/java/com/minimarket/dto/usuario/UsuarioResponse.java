package com.minimarket.dto.usuario;

import java.util.Set;

/**
 * DTO de salida de usuario. Expone los nombres de rol y nunca la contrasena.
 */
public class UsuarioResponse {

    private Long id;
    private String username;
    private Set<String> roles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
