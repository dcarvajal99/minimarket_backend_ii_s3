package com.minimarket.controller;

import com.minimarket.dto.ApiResponse;
import com.minimarket.dto.inventario.InventarioMapper;
import com.minimarket.dto.inventario.InventarioRequest;
import com.minimarket.dto.inventario.InventarioResponse;
import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.exception.ResourceNotFoundException;
import com.minimarket.service.InventarioService;
import com.minimarket.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST de inventario. Trabaja con DTOs (InventarioRequest/InventarioResponse),
 * delega la logica al servicio y devuelve respuestas uniformes ApiResponse.
 */
@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final InventarioService inventarioService;
    private final ProductoService productoService;

    public InventarioController(InventarioService inventarioService, ProductoService productoService) {
        this.inventarioService = inventarioService;
        this.productoService = productoService;
    }

    // Inventario: operacion interna (ADMIN/EMPLEADO).
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<ApiResponse<List<InventarioResponse>>> listar() {
        List<InventarioResponse> data = inventarioService.findAll().stream()
                .map(InventarioMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok("Movimientos de inventario obtenidos", data));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<ApiResponse<InventarioResponse>> obtenerPorId(@PathVariable Long id) {
        Inventario inventario = inventarioService.findById(id);
        if (inventario == null) {
            throw new ResourceNotFoundException("Inventario", id);
        }
        return ResponseEntity.ok(ApiResponse.ok("Movimiento encontrado", InventarioMapper.toResponse(inventario)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<ApiResponse<InventarioResponse>> crear(@Valid @RequestBody InventarioRequest req) {
        Producto producto = resolverProducto(req.getProductoId());
        Inventario guardado = inventarioService.save(InventarioMapper.toEntity(req, producto));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Movimiento creado", InventarioMapper.toResponse(guardado)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<ApiResponse<InventarioResponse>> actualizar(@PathVariable Long id,
                                                                      @Valid @RequestBody InventarioRequest req) {
        Inventario existente = inventarioService.findById(id);
        if (existente == null) {
            throw new ResourceNotFoundException("Inventario", id);
        }
        Producto producto = resolverProducto(req.getProductoId());
        Inventario actualizado = InventarioMapper.toEntity(req, producto);
        actualizado.setId(id);
        return ResponseEntity.ok(ApiResponse.ok("Movimiento actualizado",
                InventarioMapper.toResponse(inventarioService.save(actualizado))));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        if (inventarioService.findById(id) == null) {
            throw new ResourceNotFoundException("Inventario", id);
        }
        inventarioService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("Movimiento eliminado", null));
    }

    /** Resuelve el producto por id o lanza 404 si no existe. */
    private Producto resolverProducto(Long productoId) {
        Producto producto = productoService.findById(productoId);
        if (producto == null) {
            throw new ResourceNotFoundException("Producto", productoId);
        }
        return producto;
    }
}
