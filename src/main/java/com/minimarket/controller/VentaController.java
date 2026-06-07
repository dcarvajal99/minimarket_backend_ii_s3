package com.minimarket.controller;

import com.minimarket.dto.ApiResponse;
import com.minimarket.dto.venta.VentaMapper;
import com.minimarket.dto.venta.VentaRequest;
import com.minimarket.dto.venta.VentaResponse;
import com.minimarket.entity.Usuario;
import com.minimarket.entity.Venta;
import com.minimarket.exception.ResourceNotFoundException;
import com.minimarket.service.UsuarioService;
import com.minimarket.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST de ventas. Trabaja con DTOs (VentaRequest/VentaResponse), delega la
 * logica al servicio y devuelve respuestas uniformes ApiResponse.
 */
@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;
    private final UsuarioService usuarioService;

    public VentaController(VentaService ventaService, UsuarioService usuarioService) {
        this.ventaService = ventaService;
        this.usuarioService = usuarioService;
    }

    // Consulta de ventas: disponible para cualquier usuario autenticado.
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO', 'CLIENTE')")
    public ResponseEntity<ApiResponse<List<VentaResponse>>> listar() {
        List<VentaResponse> data = ventaService.findAll().stream()
                .map(VentaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok("Ventas obtenidas", data));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO', 'CLIENTE')")
    public ResponseEntity<ApiResponse<VentaResponse>> obtenerPorId(@PathVariable Long id) {
        Venta venta = ventaService.findById(id);
        if (venta == null) {
            throw new ResourceNotFoundException("Venta", id);
        }
        return ResponseEntity.ok(ApiResponse.ok("Venta encontrada", VentaMapper.toResponse(venta)));
    }

    // Registro de venta: cliente o personal interno.
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO', 'CLIENTE')")
    public ResponseEntity<ApiResponse<VentaResponse>> crear(@Valid @RequestBody VentaRequest req) {
        Usuario usuario = resolverUsuario(req.getUsuarioId());
        Venta guardada = ventaService.save(VentaMapper.toEntity(req, usuario));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Venta creada", VentaMapper.toResponse(guardada)));
    }

    // Modificacion de venta: solo personal interno.
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<ApiResponse<VentaResponse>> actualizar(@PathVariable Long id,
                                                                 @Valid @RequestBody VentaRequest req) {
        Venta existente = ventaService.findById(id);
        if (existente == null) {
            throw new ResourceNotFoundException("Venta", id);
        }
        Usuario usuario = resolverUsuario(req.getUsuarioId());
        Venta actualizada = VentaMapper.toEntity(req, usuario);
        actualizada.setId(id);
        return ResponseEntity.ok(ApiResponse.ok("Venta actualizada",
                VentaMapper.toResponse(ventaService.save(actualizada))));
    }

    /** Resuelve el usuario por id o lanza 404 si no existe. */
    private Usuario resolverUsuario(Long usuarioId) {
        return usuarioService.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", usuarioId));
    }
}
