package com.minimarket.controller;

import com.minimarket.dto.ApiResponse;
import com.minimarket.dto.producto.ProductoMapper;
import com.minimarket.dto.producto.ProductoRequest;
import com.minimarket.dto.producto.ProductoResponse;
import com.minimarket.entity.Categoria;
import com.minimarket.entity.Producto;
import com.minimarket.exception.ResourceNotFoundException;
import com.minimarket.service.CategoriaService;
import com.minimarket.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST de productos. Trabaja con DTOs (ProductoRequest/ProductoResponse),
 * delega la logica al servicio y devuelve respuestas uniformes ApiResponse.
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public ProductoController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    // Consulta de catalogo: disponible para cualquier usuario autenticado.
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO', 'CLIENTE')")
    public ResponseEntity<ApiResponse<List<ProductoResponse>>> listar() {
        List<ProductoResponse> data = productoService.findAll().stream()
                .map(ProductoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok("Productos obtenidos", data));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO', 'CLIENTE')")
    public ResponseEntity<ApiResponse<ProductoResponse>> obtenerPorId(@PathVariable Long id) {
        Producto producto = productoService.findById(id);
        if (producto == null) {
            throw new ResourceNotFoundException("Producto", id);
        }
        return ResponseEntity.ok(ApiResponse.ok("Producto encontrado", ProductoMapper.toResponse(producto)));
    }

    // Alta de productos: reservado al personal interno.
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<ApiResponse<ProductoResponse>> crear(@Valid @RequestBody ProductoRequest req) {
        Categoria categoria = resolverCategoria(req.getCategoriaId());
        Producto guardado = productoService.save(ProductoMapper.toEntity(req, categoria));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Producto creado", ProductoMapper.toResponse(guardado)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<ApiResponse<ProductoResponse>> actualizar(@PathVariable Long id,
                                                                    @Valid @RequestBody ProductoRequest req) {
        Producto existente = productoService.findById(id);
        if (existente == null) {
            throw new ResourceNotFoundException("Producto", id);
        }
        Categoria categoria = resolverCategoria(req.getCategoriaId());
        Producto actualizado = ProductoMapper.toEntity(req, categoria);
        actualizado.setId(id);
        return ResponseEntity.ok(ApiResponse.ok("Producto actualizado",
                ProductoMapper.toResponse(productoService.save(actualizado))));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        if (productoService.findById(id) == null) {
            throw new ResourceNotFoundException("Producto", id);
        }
        productoService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("Producto eliminado", null));
    }

    /** Resuelve la categoria por id o lanza 404 si no existe. */
    private Categoria resolverCategoria(Long categoriaId) {
        Categoria categoria = categoriaService.findById(categoriaId);
        if (categoria == null) {
            throw new ResourceNotFoundException("Categoria", categoriaId);
        }
        return categoria;
    }
}
