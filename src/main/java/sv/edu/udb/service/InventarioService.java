package sv.edu.udb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.exception.ResourceNotFoundException;
import sv.edu.udb.model.Inventario;
import sv.edu.udb.repository.InventarioRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class InventarioService {

    @Autowired
    private InventarioRepository inventarioRepository;

    public List<Inventario> listarTodos() {
        return inventarioRepository.findAll();
    }

    public Inventario buscarPorId(Long id) {
        return inventarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado: " + id));
    }

    public List<Inventario> porSucursal(Long sucursalId) {
        return inventarioRepository.findBySucursalId(sucursalId);
    }

    public List<Inventario> stockBajo() {
        return inventarioRepository.findAll().stream()
                .filter(i -> i.getStock() != null && i.getStockMinimo() != null)
                .filter(i -> i.getStock() <= i.getStockMinimo())
                .toList();
    }

    public Inventario crear(Inventario inventario) {
        inventario.setUltimaActualizacion(LocalDateTime.now());
        return inventarioRepository.save(inventario);
    }

    public Inventario actualizar(Long id, Inventario nuevo) {
        Inventario existente = buscarPorId(id);
        existente.setProducto(nuevo.getProducto());
        existente.setSucursal(nuevo.getSucursal());
        existente.setStock(nuevo.getStock());
        existente.setStockMinimo(nuevo.getStockMinimo());
        existente.setUltimaActualizacion(LocalDateTime.now());
        return inventarioRepository.save(existente);
    }

    public Inventario ajustar(Long productoId, Long sucursalId, Integer cantidad) {
        Inventario inventario = inventarioRepository.findByProductoIdAndSucursalId(productoId, sucursalId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado para producto y sucursal"));
        inventario.setStock((inventario.getStock() == null ? 0 : inventario.getStock()) + cantidad);
        inventario.setUltimaActualizacion(LocalDateTime.now());
        return inventarioRepository.save(inventario);
    }
}
