package sv.edu.udb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.edu.udb.model.Inventario;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    List<Inventario> findBySucursalId(Long sucursalId);
    List<Inventario> findByStockLessThanEqual(Integer stock);
    Optional<Inventario> findByProductoIdAndSucursalId(Long productoId, Long sucursalId);
}
// Extender JpaRepository<ENTIDAD, Long>

