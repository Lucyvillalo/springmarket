package sv.edu.udb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.exception.ResourceNotFoundException;
import sv.edu.udb.model.Categoria;
import sv.edu.udb.repository.CategoriaRepository;

import java.util.List;

@Service
@Transactional
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Categoria> listarTodos() {
        return categoriaRepository.findAll();
    }

    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada: " + id));
    }

    public Categoria crear(Categoria categoria) {
        if (categoriaRepository.existsByNombreIgnoreCase(categoria.getNombre())) {
            throw new RuntimeException("Ya existe una categoria con ese nombre");
        }
        return categoriaRepository.save(categoria);
    }

    public Categoria actualizar(Long id, Categoria nueva) {
        Categoria existente = buscarPorId(id);
        existente.setNombre(nueva.getNombre());
        return categoriaRepository.save(existente);
    }

    public void eliminar(Long id) {
        buscarPorId(id);
        categoriaRepository.deleteById(id);
    }
}
