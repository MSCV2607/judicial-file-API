package com.grupoNoctua.judicial_file_API.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "expediente")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Expediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String dni;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario encargado;

    @Column(name = "ultima_actualizacion")
    private java.time.LocalDateTime ultimaActualizacion;
}
