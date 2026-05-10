package sv.edu.udb.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.model.Inventario;
import sv.edu.udb.service.InventarioService;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
@CrossOrigin(origins = "*")
@PreAuthorize("hasAnyRole('ADMIN','CAJERO')")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @GetMapping
    public ResponseEntity<List<Inventario>> listar() {
        return ResponseEntity.ok(inventarioService.listarTodos());
    }

    @GetMapping("/sucursal/{id}")
    public ResponseEntity<List<Inventario>> porSucursal(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.porSucursal(id));
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<List<Inventario>> stockBajo() {
        return ResponseEntity.ok(inventarioService.stockBajo());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Inventario> crear(@Valid @RequestBody Inventario inventario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.crear(inventario));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Inventario> actualizar(@PathVariable Long id, @Valid @RequestBody Inventario inventario) {
        return ResponseEntity.ok(inventarioService.actualizar(id, inventario));
    }

    @PutMapping("/ajustar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Inventario> ajustar(@RequestParam Long productoId,
                                              @RequestParam Long sucursalId,
                                              @RequestParam Integer cantidad) {
        return ResponseEntity.ok(inventarioService.ajustar(productoId, sucursalId, cantidad));
    }
}
