package sv.edu.udb.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.model.Sucursal;
import sv.edu.udb.service.SucursalService;

import java.util.List;

@RestController
@RequestMapping("/api/sucursales")
@CrossOrigin(origins = "*")
public class SucursalController {

    @Autowired
    private SucursalService sucursalService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CAJERO')")
    public ResponseEntity<List<Sucursal>> listar() {
        return ResponseEntity.ok(sucursalService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CAJERO')")
    public ResponseEntity<Sucursal> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(sucursalService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Sucursal> crear(@Valid @RequestBody Sucursal sucursal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sucursalService.crear(sucursal));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Sucursal> actualizar(@PathVariable Long id, @Valid @RequestBody Sucursal sucursal) {
        return ResponseEntity.ok(sucursalService.actualizar(id, sucursal));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        sucursalService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
