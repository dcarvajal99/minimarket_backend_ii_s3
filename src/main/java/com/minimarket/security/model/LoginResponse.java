package com.minimarket.security.model;

import java.util.List;

/**
 * DTO de respuesta del login: devuelve el token JWT junto con el username y
 * los roles del usuario autenticado, evitando exponer la entidad Usuario.
 */
public class LoginResponse {

    private String token;
    private String tipo = "Bearer";
    private String username;
    private List<String> roles;

    public LoginResponse(String token, String username, List<String> roles) {
        this.token = token;
        this.username = username;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
