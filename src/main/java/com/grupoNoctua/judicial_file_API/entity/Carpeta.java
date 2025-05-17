package com.grupoNoctua.judicial_file_API.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "carpeta")
public class Carpeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_carpeta", unique = true, nullable = false)
    private String numeroCarpeta; // ej: DNI

    @Column(length = 500)
    private String descripcion;

    private String estado;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    @Column(name = "ultima_actualizacion")
    private LocalDateTime ultimaActualizacion;

    private String directorio; // ruta física en disco

    @Column(name = "tipo_archivo")
    private String tipoArchivo;

    // Relación N:M con Usuario (encargados)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "encargado_carpeta",
            joinColumns = @JoinColumn(name = "carpeta_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private Set<Usuario> encargados;

    // Relación N:M con Cliente (dueños)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "cliente_carpeta",
            joinColumns = @JoinColumn(name = "carpeta_id"),
            inverseJoinColumns = @JoinColumn(name = "cliente_id")
    )
    private Set<Cliente> clientes;

    // CONSTRUCTORES

    public Carpeta() {
    }

    public Carpeta(Long id, String numeroCarpeta, String descripcion, String estado,
                   LocalDate fechaCreacion, LocalDateTime ultimaActualizacion,
                   String directorio, String tipoArchivo,
                   Set<Usuario> encargados, Set<Cliente> clientes) {
        this.id = id;
        this.numeroCarpeta = numeroCarpeta;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.ultimaActualizacion = ultimaActualizacion;
        this.directorio = directorio;
        this.tipoArchivo = tipoArchivo;
        this.encargados = encargados;
        this.clientes = clientes;
    }

    // GETTERS Y SETTERS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroCarpeta() {
        return numeroCarpeta;
    }

    public void setNumeroCarpeta(String numeroCarpeta) {
        this.numeroCarpeta = numeroCarpeta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    public void setUltimaActualizacion(LocalDateTime ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }

    public String getDirectorio() {
        return directorio;
    }

    public void setDirectorio(String directorio) {
        this.directorio = directorio;
    }

    public String getTipoArchivo() {
        return tipoArchivo;
    }

    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
    }

    public Set<Usuario> getEncargados() {
        return encargados;
    }

    public void setEncargados(Set<Usuario> encargados) {
        this.encargados = encargados;
    }

    public Set<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(Set<Cliente> clientes) {
        this.clientes = clientes;
    }
}

