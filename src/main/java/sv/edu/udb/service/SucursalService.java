package sv.edu.udb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.exception.ResourceNotFoundException;
import sv.edu.udb.model.Sucursal;
import sv.edu.udb.repository.SucursalRepository;

import java.util.List;

@Service
@Transactional
public class SucursalService {

    @Autowired
    private SucursalRepository sucursalRepository;

    public List<Sucursal> listarTodos() {
        return sucursalRepository.findAll();
    }

    public Sucursal buscarPorId(Long id) {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada: " + id));
    }

    public Sucursal crear(Sucursal sucursal) {
        return sucursalRepository.save(sucursal);
    }

    public Sucursal actualizar(Long id, Sucursal nueva) {
        Sucursal existente = buscarPorId(id);
        existente.setNombre(nueva.getNombre());
        existente.setDireccion(nueva.getDireccion());
        existente.setTelefono(nueva.getTelefono());
        existente.setEncargado(nueva.getEncargado());
        return sucursalRepository.save(existente);
    }

    public void eliminar(Long id) {
        buscarPorId(id);
        sucursalRepository.deleteById(id);
    }
}
