package com.minimarket.controller;

import com.minimarket.dto.ApiResponse;
import com.minimarket.dto.categoria.CategoriaMapper;
import com.minimarket.dto.categoria.CategoriaRequest;
import com.minimarket.dto.categoria.CategoriaResponse;
import com.minimarket.entity.Categoria;
import com.minimarket.exception.ResourceNotFoundException;
import com.minimarket.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST de categorias. Trabaja con DTOs (CategoriaRequest/CategoriaResponse),
 * delega la logica al servicio y devuelve respuestas uniformes ApiResponse.
 */
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    // Consulta de catalogo: disponible para cualquier usuario autenticado.
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO', 'CLIENTE')")
    public ResponseEntity<ApiResponse<List<CategoriaResponse>>> listar() {
        List<CategoriaResponse> data = categoriaService.findAll().stream()
                .map(CategoriaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok("Categorias obtenidas", data));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO', 'CLIENTE')")
    public ResponseEntity<ApiResponse<CategoriaResponse>> obtenerPorId(@PathVariable Long id) {
        Categoria categoria = categoriaService.findById(id);
        if (categoria == null) {
            throw new ResourceNotFoundException("Categoria", id);
        }
        return ResponseEntity.ok(ApiResponse.ok("Categoria encontrada", CategoriaMapper.toResponse(categoria)));
    }

    // Alta de categorias: reservado al personal interno.
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<ApiResponse<CategoriaResponse>> crear(@Valid @RequestBody CategoriaRequest req) {
        Categoria guardada = categoriaService.save(CategoriaMapper.toEntity(req));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Categoria creada", CategoriaMapper.toResponse(guardada)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<ApiResponse<CategoriaResponse>> actualizar(@PathVariable Long id,
                                                                     @Valid @RequestBody CategoriaRequest req) {
        Categoria existente = categoriaService.findById(id);
        if (existente == null) {
            throw new ResourceNotFoundException("Categoria", id);
        }
        Categoria actualizada = CategoriaMapper.toEntity(req);
        actualizada.setId(id);
        return ResponseEntity.ok(ApiResponse.ok("Categoria actualizada",
                CategoriaMapper.toResponse(categoriaService.save(actualizada))));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        if (categoriaService.findById(id) == null) {
            throw new ResourceNotFoundException("Categoria", id);
        }
        categoriaService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("Categoria eliminada", null));
    }
}
