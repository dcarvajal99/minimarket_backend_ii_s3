package com.minimarket.controller;

import com.minimarket.entity.Venta;
import com.minimarket.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    // Consulta de ventas: usuario autenticado
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO', 'CLIENTE')")
    @GetMapping
    public List<Venta> listarVentas() {
        return ventaService.findAll();
    }

    // Consulta de ventas: usuario autenticado
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO', 'CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<Venta> obtenerVentaPorId(@PathVariable Long id) {
        Venta venta = ventaService.findById(id);
        return (venta != null) ? ResponseEntity.ok(venta) : ResponseEntity.notFound().build();
    }

    // Registro de venta: cliente o personal interno
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO', 'CLIENTE')")
    @PostMapping
    public Venta guardarVenta(@RequestBody Venta venta) {
        return ventaService.save(venta);
    }
}
