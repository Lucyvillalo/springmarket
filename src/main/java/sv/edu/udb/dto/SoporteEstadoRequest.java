package sv.edu.udb.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SoporteEstadoRequest {
    @NotBlank(message = "El estado es obligatorio")
    private String estado;
}
