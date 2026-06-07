package com.minimarket.controller;

import com.minimarket.entity.DetalleVenta;
import com.minimarket.service.DetalleVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/detalle-ventas")
public class DetalleVentaController {

    @Autowired
    private DetalleVentaService detalleVentaService;

    // Detalle de venta: usuario autenticado
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO', 'CLIENTE')")
    @GetMapping
    public List<DetalleVenta> listarDetalleVentas() {
        return detalleVentaService.findAll();
    }

    // Detalle de venta: usuario autenticado
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO', 'CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<DetalleVenta> obtenerDetalleVentaPorId(@PathVariable Long id) {
        DetalleVenta detalleVenta = detalleVentaService.findById(id);
        return (detalleVenta != null) ? ResponseEntity.ok(detalleVenta) : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO', 'CLIENTE')")
    @PostMapping
    public DetalleVenta guardarDetalleVenta(@RequestBody DetalleVenta detalleVenta) {
        return detalleVentaService.save(detalleVenta);
    }

    // Modificacion de detalle: solo personal interno
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    @PutMapping("/{id}")
    public ResponseEntity<DetalleVenta> actualizarDetalleVenta(@PathVariable Long id, @RequestBody DetalleVenta detalleVenta) {
        DetalleVenta existente = detalleVentaService.findById(id);
        if (existente != null) {
            detalleVenta.setId(id);
            return ResponseEntity.ok(detalleVentaService.save(detalleVenta));
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDetalleVenta(@PathVariable Long id) {
        DetalleVenta detalleVenta = detalleVentaService.findById(id);
        if (detalleVenta != null) {
            detalleVentaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
