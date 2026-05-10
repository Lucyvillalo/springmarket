package sv.edu.udb.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.model.Venta;
import sv.edu.udb.service.VentaService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Venta>> listar() {
        return ResponseEntity.ok(ventaService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CAJERO','CLIENTE')")
    public ResponseEntity<Venta> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.buscarPermitida(id));
    }

    @GetMapping("/sucursal/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Venta>> porSucursal(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.porSucursal(id));
    }

    @GetMapping("/cliente/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Venta>> porCliente(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.porCliente(id));
    }

    @GetMapping("/empleado/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Venta>> porEmpleado(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.porEmpleado(id));
    }

    @GetMapping("/mis-ventas")
    @PreAuthorize("hasRole('CAJERO')")
    public ResponseEntity<List<Venta>> misVentas() {
        return ResponseEntity.ok(ventaService.ventasDelUsuarioActual());
    }

    @GetMapping("/mis-compras")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<Venta>> misCompras() {
        return ResponseEntity.ok(ventaService.comprasDelClienteActual());
    }

    @GetMapping("/hoy")
    @PreAuthorize("hasAnyRole('ADMIN','CAJERO')")
    public ResponseEntity<List<Venta>> hoy() {
        return ResponseEntity.ok(ventaService.hoy());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CAJERO','CLIENTE')")
    public ResponseEntity<Venta> registrar(@Valid @RequestBody Venta venta) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaService.registrar(venta));
    }

    @PutMapping("/{id}/devolucion")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Venta> devolucion(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.marcarDevolucion(id));
    }

    @GetMapping("/reporte/global")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> reporteGlobal() {
        return ResponseEntity.ok(ventaService.reporteGlobal());
    }

    @GetMapping("/reporte/sucursal/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> reporteSucursal(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.reporteSucursal(id));
    }

    @GetMapping("/reporte/mis-ventas")
    @PreAuthorize("hasRole('CAJERO')")
    public ResponseEntity<Map<String, Object>> reporteMisVentas() {
        return ResponseEntity.ok(ventaService.reporteEmpleadoActual());
    }

    @GetMapping("/reporte/mis-compras")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Map<String, Object>> reporteMisCompras() {
        return ResponseEntity.ok(ventaService.reporteClienteActual());
    }
}
