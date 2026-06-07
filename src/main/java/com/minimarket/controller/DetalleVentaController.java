package com.minimarket.controller;

import com.minimarket.dto.ApiResponse;
import com.minimarket.dto.detalleventa.DetalleVentaMapper;
import com.minimarket.dto.detalleventa.DetalleVentaRequest;
import com.minimarket.dto.detalleventa.DetalleVentaResponse;
import com.minimarket.entity.DetalleVenta;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Venta;
import com.minimarket.exception.ResourceNotFoundException;
import com.minimarket.service.DetalleVentaService;
import com.minimarket.service.ProductoService;
import com.minimarket.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST de detalles de venta. Trabaja con DTOs
 * (DetalleVentaRequest/DetalleVentaResponse), delega la logica al servicio y
 * devuelve respuestas uniformes ApiResponse.
 */
@RestController
@RequestMapping("/api/detalle-ventas")
public class DetalleVentaController {

    private final DetalleVentaService detalleVentaService;
    private final VentaService ventaService;
    private final ProductoService productoService;

    public DetalleVentaController(DetalleVentaService detalleVentaService,
                                  VentaService ventaService,
                                  ProductoService productoService) {
        this.detalleVentaService = detalleVentaService;
        this.ventaService = ventaService;
        this.productoService = productoService;
    }

    // Consulta de detalles: disponible para cualquier usuario autenticado.
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO', 'CLIENTE')")
    public ResponseEntity<ApiResponse<List<DetalleVentaResponse>>> listar() {
        List<DetalleVentaResponse> data = detalleVentaService.findAll().stream()
                .map(DetalleVentaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok("Detalles de venta obtenidos", data));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO', 'CLIENTE')")
    public ResponseEntity<ApiResponse<DetalleVentaResponse>> obtenerPorId(@PathVariable Long id) {
        DetalleVenta detalle = detalleVentaService.findById(id);
        if (detalle == null) {
            throw new ResourceNotFoundException("DetalleVenta", id);
        }
        return ResponseEntity.ok(ApiResponse.ok("Detalle de venta encontrado",
                DetalleVentaMapper.toResponse(detalle)));
    }

    // Alta de detalle: cliente o personal interno.
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO', 'CLIENTE')")
    public ResponseEntity<ApiResponse<DetalleVentaResponse>> crear(@Valid @RequestBody DetalleVentaRequest req) {
        Venta venta = resolverVenta(req.getVentaId());
        Producto producto = resolverProducto(req.getProductoId());
        DetalleVenta guardado = detalleVentaService.save(DetalleVentaMapper.toEntity(req, venta, producto));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Detalle de venta creado", DetalleVentaMapper.toResponse(guardado)));
    }

    // Modificacion de detalle: solo personal interno.
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<ApiResponse<DetalleVentaResponse>> actualizar(@PathVariable Long id,
                                                                        @Valid @RequestBody DetalleVentaRequest req) {
        DetalleVenta existente = detalleVentaService.findById(id);
        if (existente == null) {
            throw new ResourceNotFoundException("DetalleVenta", id);
        }
        Venta venta = resolverVenta(req.getVentaId());
        Producto producto = resolverProducto(req.getProductoId());
        DetalleVenta actualizado = DetalleVentaMapper.toEntity(req, venta, producto);
        actualizado.setId(id);
        return ResponseEntity.ok(ApiResponse.ok("Detalle de venta actualizado",
                DetalleVentaMapper.toResponse(detalleVentaService.save(actualizado))));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        if (detalleVentaService.findById(id) == null) {
            throw new ResourceNotFoundException("DetalleVenta", id);
        }
        detalleVentaService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("Detalle de venta eliminado", null));
    }

    /** Resuelve la venta por id o lanza 404 si no existe. */
    private Venta resolverVenta(Long ventaId) {
        Venta venta = ventaService.findById(ventaId);
        if (venta == null) {
            throw new ResourceNotFoundException("Venta", ventaId);
        }
        return venta;
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
