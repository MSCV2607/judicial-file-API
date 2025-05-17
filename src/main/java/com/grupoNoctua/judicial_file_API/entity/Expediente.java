package com.grupoNoctua.judicial_file_API.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "expedientes")
public class Expediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dni;
    private String nombre;
    private String apellido;

    private LocalDateTime ultimaActualizacion;

    @ManyToOne
    @JoinColumn(name = "encargado_id")
    private Usuario encargado;

    @ManyToMany
    @JoinTable(
            name = "expediente_colaboradores",
            joinColumns = @JoinColumn(name = "expediente_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private Set<Usuario> colaboradores = new HashSet<>();

    // Getters y setters

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getDni() { return dni; }

    public void setDni(String dni) { this.dni = dni; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }

    public void setApellido(String apellido) { this.apellido = apellido; }

    public LocalDateTime getUltimaActualizacion() { return ultimaActualizacion; }

    public void setUltimaActualizacion(LocalDateTime ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }

    public Usuario getEncargado() { return encargado; }

    public void setEncargado(Usuario encargado) { this.encargado = encargado; }

    public Set<Usuario> getColaboradores() { return colaboradores; }

    public void setColaboradores(Set<Usuario> colaboradores) {
        this.colaboradores = colaboradores;
    }
}
