package com.minimarket.controller;

import com.minimarket.dto.ApiResponse;
import com.minimarket.dto.usuario.UsuarioMapper;
import com.minimarket.dto.usuario.UsuarioRequest;
import com.minimarket.dto.usuario.UsuarioResponse;
import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.exception.ResourceNotFoundException;
import com.minimarket.service.RolService;
import com.minimarket.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Gestion de usuarios. Trabaja con DTOs (UsuarioRequest/UsuarioResponse), cifra
 * la contrasena con BCrypt y nunca la expone. Toda la administracion de cuentas
 * queda restringida al rol ADMIN, tanto a nivel de URL (SecurityConfig) como de
 * clase (@PreAuthorize), aplicando defensa en profundidad.
 */
@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioService usuarioService, RolService rolService,
                            PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UsuarioResponse>>> listar() {
        List<UsuarioResponse> data = usuarioService.findAll().stream()
                .map(UsuarioMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok("Usuarios obtenidos", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> obtenerPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
        return ResponseEntity.ok(ApiResponse.ok("Usuario encontrado", UsuarioMapper.toResponse(usuario)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UsuarioResponse>> crear(@Valid @RequestBody UsuarioRequest req) {
        Usuario usuario = new Usuario();
        usuario.setUsername(req.getUsername());
        usuario.setPassword(passwordEncoder.encode(req.getPassword()));
        usuario.setRoles(resolverRoles(req.getRoles()));
        Usuario guardado = usuarioService.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Usuario creado", UsuarioMapper.toResponse(guardado)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> actualizar(@PathVariable Long id,
                                                                   @Valid @RequestBody UsuarioRequest req) {
        Usuario existente = usuarioService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
        existente.setUsername(req.getUsername());
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            existente.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        existente.setRoles(resolverRoles(req.getRoles()));
        return ResponseEntity.ok(ApiResponse.ok("Usuario actualizado",
                UsuarioMapper.toResponse(usuarioService.save(existente))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        if (usuarioService.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Usuario", id);
        }
        usuarioService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("Usuario eliminado", null));
    }

    /** Resuelve cada nombre de rol a su entidad o lanza 404 si no existe. */
    private Set<Rol> resolverRoles(Set<String> nombres) {
        Set<Rol> roles = new HashSet<>();
        for (String nombre : nombres) {
            Rol rol = rolService.findByNombre(nombre)
                    .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + nombre));
            roles.add(rol);
        }
        return roles;
    }
}
