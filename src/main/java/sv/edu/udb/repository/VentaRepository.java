package sv.edu.udb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.edu.udb.model.Venta;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findBySucursalId(Long sucursalId);
    List<Venta> findByClienteId(Long clienteId);
    List<Venta> findByEmpleadoId(Long empleadoId);
    List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
    List<Venta> findByEmpleadoIdAndFechaBetween(Long empleadoId, LocalDateTime inicio, LocalDateTime fin);
    List<Venta> findByClienteIdAndFechaBetween(Long clienteId, LocalDateTime inicio, LocalDateTime fin);
}
// Extender JpaRepository<ENTIDAD, Long>

