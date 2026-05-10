package sv.edu.udb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.exception.ResourceNotFoundException;
import sv.edu.udb.model.Producto;
import sv.edu.udb.repository.ProductoRepository;

import java.util.List;

@Service
@Transactional
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public List<Producto> disponibles() {
        return productoRepository.findByStockGreaterThan(0);
    }

    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id));
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Producto> porCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId);
    }

    public Producto crear(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto actualizar(Long id, Producto nuevo) {
        Producto existente = buscarPorId(id);
        existente.setNombre(nuevo.getNombre());
        existente.setPrecio(nuevo.getPrecio());
        existente.setStock(nuevo.getStock());
        existente.setCategoria(nuevo.getCategoria());
        existente.setProveedor(nuevo.getProveedor());
        return productoRepository.save(existente);
    }

    public void eliminar(Long id) {
        buscarPorId(id);
        productoRepository.deleteById(id);
    }
}
