package sv.edu.udb.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.dto.SoporteEstadoRequest;
import sv.edu.udb.dto.SoporteReporteRequest;
import sv.edu.udb.dto.SoporteReporteResponse;
import sv.edu.udb.service.SoporteReporteService;

import java.util.List;

@RestController
@RequestMapping("/api/soporte")
@CrossOrigin(origins = "*")
public class SoporteReporteController {

    @Autowired
    private SoporteReporteService soporteService;

    @PostMapping
    public ResponseEntity<SoporteReporteResponse> crear(@Valid @RequestBody SoporteReporteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(soporteService.crear(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SoporteReporteResponse>> listar() {
        return ResponseEntity.ok(soporteService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SoporteReporteResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(soporteService.buscarPorId(id));
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SoporteReporteResponse> actualizarEstado(@PathVariable Long id,
                                                                   @Valid @RequestBody SoporteEstadoRequest request) {
        return ResponseEntity.ok(soporteService.actualizarEstado(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        soporteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
