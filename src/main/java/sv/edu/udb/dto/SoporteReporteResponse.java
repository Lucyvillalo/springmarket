package sv.edu.udb.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SoporteReporteResponse {
    private Long id;
    private String titulo;
    private String tipoProblema;
    private String descripcion;
    private String prioridad;
    private String estado;
    private LocalDateTime fechaCreacion;
    private Long clienteId;
    private String clienteNombre;
    private Long empleadoId;
    private String empleadoNombre;
    private Long sucursalId;
    private String sucursalNombre;
    private String usuario;
}
