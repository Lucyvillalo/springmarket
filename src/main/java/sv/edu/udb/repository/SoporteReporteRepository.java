package sv.edu.udb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.edu.udb.model.SoporteReporte;

import java.util.List;

@Repository
public interface SoporteReporteRepository extends JpaRepository<SoporteReporte, Long> {
    List<SoporteReporte> findAllByOrderByFechaCreacionDesc();
}
