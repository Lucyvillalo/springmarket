package sv.edu.udb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "proveedor")
@Data
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    private String contacto;
    private String telefono;

    @Email(message = "Email invalido")
    private String email;

    private String direccion;
}
// Agregar anotaciones @Entity, @Table, @Data, @Id, etc.

