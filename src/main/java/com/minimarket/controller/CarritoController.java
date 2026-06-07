package com.minimarket.controller;

import com.minimarket.entity.Carrito;
import com.minimarket.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    // Carrito de compras: operacion del cliente
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    @GetMapping
    public List<Carrito> listarCarrito() {
        return carritoService.findAll();
    }

    // Carrito de compras: operacion del cliente
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<Carrito> obtenerCarritoPorId(@PathVariable Long id) {
        Carrito carrito = carritoService.findById(id);
        return (carrito != null) ? ResponseEntity.ok(carrito) : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    @PostMapping
    public Carrito agregarProductoAlCarrito(@RequestBody Carrito carrito) {
        return carritoService.save(carrito);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    @PutMapping("/{id}")
    public ResponseEntity<Carrito> actualizarCarrito(@PathVariable Long id, @RequestBody Carrito carrito) {
        Carrito existente = carritoService.findById(id);
        if (existente != null) {
            carrito.setId(id);
            return ResponseEntity.ok(carritoService.save(carrito));
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProductoDelCarrito(@PathVariable Long id) {
        Carrito carrito = carritoService.findById(id);
        if (carrito != null) {
            carritoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
