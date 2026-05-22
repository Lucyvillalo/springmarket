package sv.edu.udb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SoporteReporteRequest {

    @NotBlank(message = "El titulo es obligatorio")
    @Size(max = 150, message = "El titulo no puede superar 150 caracteres")
    private String titulo;

    @NotBlank(message = "El tipo de problema es obligatorio")
    private String tipoProblema;

    @NotBlank(message = "La descripcion es obligatoria")
    private String descripcion;

    private String prioridad;
    private Long sucursalId;
}
