package sv.edu.udb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.exception.ResourceNotFoundException;
import sv.edu.udb.model.Cliente;
import sv.edu.udb.repository.ClienteRepository;

import java.util.List;

@Service
@Transactional
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + id));
    }

    public Cliente registrar(Cliente cliente) {
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new RuntimeException("Ya existe un cliente con ese email");
        }
        cliente.setPassword(passwordEncoder.encode(cliente.getPassword()));
        return clienteRepository.save(cliente);
    }

    public Cliente actualizar(Long id, Cliente nuevo) {
        Cliente existente = buscarPorId(id);
        existente.setNombre(nuevo.getNombre());
        existente.setTelefono(nuevo.getTelefono());
        existente.setDireccion(nuevo.getDireccion());
        return clienteRepository.save(existente);
    }

    public void eliminar(Long id) {
        buscarPorId(id);
        clienteRepository.deleteById(id);
    }
}
