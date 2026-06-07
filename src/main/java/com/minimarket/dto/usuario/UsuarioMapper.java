package com.minimarket.dto.usuario;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper entre la entidad Usuario y sus DTOs. Centraliza la conversion para
 * mantener los controladores limpios y desacoplados de la entidad JPA. El
 * cifrado de la contrasena y la resolucion de roles se realizan en el
 * controlador, por eso aqui solo se expone la conversion de salida.
 */
public final class UsuarioMapper {

    private UsuarioMapper() {
    }

    /** Convierte la entidad a su DTO de salida, mapeando los roles a sus nombres. */
    public static UsuarioResponse toResponse(Usuario usuario) {
        UsuarioResponse dto = new UsuarioResponse();
        dto.setId(usuario.getId());
        dto.setUsername(usuario.getUsername());
        if (usuario.getRoles() != null) {
            Set<String> nombres = usuario.getRoles().stream()
                    .map(Rol::getNombre)
                    .collect(Collectors.toCollection(HashSet::new));
            dto.setRoles(nombres);
        }
        return dto;
    }
}
