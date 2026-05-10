package sv.edu.udb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventario")
@Data
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_producto")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Producto producto;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_sucursal")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Sucursal sucursal;

    private Integer stock = 0;

    @Column(name = "stock_minimo")
    private Integer stockMinimo = 0;

    @Column(name = "ultima_actualizacion")
    private LocalDateTime ultimaActualizacion;
}

