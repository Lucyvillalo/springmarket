package sv.edu.udb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.exception.ResourceNotFoundException;
import sv.edu.udb.model.Proveedor;
import sv.edu.udb.repository.ProveedorRepository;

import java.util.List;

@Service
@Transactional
public class ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    public List<Proveedor> listarTodos() {
        return proveedorRepository.findAll();
    }

    public Proveedor buscarPorId(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado: " + id));
    }

    public Proveedor crear(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    public Proveedor actualizar(Long id, Proveedor nuevo) {
        Proveedor existente = buscarPorId(id);
        existente.setNombre(nuevo.getNombre());
        existente.setContacto(nuevo.getContacto());
        existente.setTelefono(nuevo.getTelefono());
        existente.setEmail(nuevo.getEmail());
        existente.setDireccion(nuevo.getDireccion());
        return proveedorRepository.save(existente);
    }

    public void eliminar(Long id) {
        buscarPorId(id);
        proveedorRepository.deleteById(id);
    }
}
