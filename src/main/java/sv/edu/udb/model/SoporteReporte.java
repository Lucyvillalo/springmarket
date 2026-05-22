package sv.edu.udb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "soporte_reporte")
@Data
public class SoporteReporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El titulo es obligatorio")
    @Column(nullable = false, length = 150)
    private String titulo;

    @NotBlank(message = "El tipo de problema es obligatorio")
    @Column(name = "tipo_problema", nullable = false, length = 50)
    private String tipoProblema;

    @NotBlank(message = "La descripcion es obligatoria")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 20)
    private String prioridad = "MEDIA";

    @Column(length = 20)
    private String estado = "PENDIENTE";

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    @JsonIgnoreProperties({"password", "hibernateLazyInitializer", "handler"})
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_empleado")
    @JsonIgnoreProperties({"password", "hibernateLazyInitializer", "handler"})
    private Empleado empleado;

    @ManyToOne
    @JoinColumn(name = "id_sucursal")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Sucursal sucursal;

    @PrePersist
    void prePersist() {
        if (fechaCreacion == null) fechaCreacion = LocalDateTime.now();
        if (estado == null || estado.isBlank()) estado = "PENDIENTE";
        if (prioridad == null || prioridad.isBlank()) prioridad = "MEDIA";
    }
}
