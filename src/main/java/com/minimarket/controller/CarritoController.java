package com.minimarket.controller;

import com.minimarket.dto.ApiResponse;
import com.minimarket.dto.carrito.CarritoMapper;
import com.minimarket.dto.carrito.CarritoRequest;
import com.minimarket.dto.carrito.CarritoResponse;
import com.minimarket.entity.Carrito;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Usuario;
import com.minimarket.exception.ResourceNotFoundException;
import com.minimarket.service.CarritoService;
import com.minimarket.service.ProductoService;
import com.minimarket.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST del carrito. Trabaja con DTOs (CarritoRequest/CarritoResponse),
 * delega la logica al servicio y devuelve respuestas uniformes ApiResponse.
 */
@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    private final CarritoService carritoService;
    private final UsuarioService usuarioService;
    private final ProductoService productoService;

    public CarritoController(CarritoService carritoService, UsuarioService usuarioService,
                            ProductoService productoService) {
        this.carritoService = carritoService;
        this.usuarioService = usuarioService;
        this.productoService = productoService;
    }

    // Carrito de compras: operacion del cliente.
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<ApiResponse<List<CarritoResponse>>> listar() {
        List<CarritoResponse> data = carritoService.findAll().stream()
                .map(CarritoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok("Items del carrito obtenidos", data));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<ApiResponse<CarritoResponse>> obtenerPorId(@PathVariable Long id) {
        Carrito carrito = carritoService.findById(id);
        if (carrito == null) {
            throw new ResourceNotFoundException("Carrito", id);
        }
        return ResponseEntity.ok(ApiResponse.ok("Item del carrito encontrado", CarritoMapper.toResponse(carrito)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<ApiResponse<CarritoResponse>> crear(@Valid @RequestBody CarritoRequest req) {
        Usuario usuario = resolverUsuario(req.getUsuarioId());
        Producto producto = resolverProducto(req.getProductoId());
        Carrito guardado = carritoService.save(CarritoMapper.toEntity(req, usuario, producto));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Producto agregado al carrito", CarritoMapper.toResponse(guardado)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<ApiResponse<CarritoResponse>> actualizar(@PathVariable Long id,
                                                                   @Valid @RequestBody CarritoRequest req) {
        Carrito existente = carritoService.findById(id);
        if (existente == null) {
            throw new ResourceNotFoundException("Carrito", id);
        }
        Usuario usuario = resolverUsuario(req.getUsuarioId());
        Producto producto = resolverProducto(req.getProductoId());
        Carrito actualizado = CarritoMapper.toEntity(req, usuario, producto);
        actualizado.setId(id);
        return ResponseEntity.ok(ApiResponse.ok("Carrito actualizado",
                CarritoMapper.toResponse(carritoService.save(actualizado))));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        if (carritoService.findById(id) == null) {
            throw new ResourceNotFoundException("Carrito", id);
        }
        carritoService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("Producto eliminado del carrito", null));
    }

    /** Resuelve el usuario por id o lanza 404 si no existe. */
    private Usuario resolverUsuario(Long usuarioId) {
        return usuarioService.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", usuarioId));
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
