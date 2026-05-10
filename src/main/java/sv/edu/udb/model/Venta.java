package sv.edu.udb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "venta")
@Data
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    private String estado = "COMPLETADA";

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

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("venta")
    private List<DetalleVenta> detalles = new ArrayList<>();

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("venta")
    private List<Pago> pagos = new ArrayList<>();
}
// Agregar anotaciones @Entity, @Table, @Data, @Id, etc.

