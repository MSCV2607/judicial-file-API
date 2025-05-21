package com.grupoNoctua.judicial_file_API.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    private Long id;

    private Integer edad;

    private String telefono;
    private String correo;

    public Cliente() {}

    public Cliente(Long id, Integer edad, String telefono, String correo) {
        this.id = id;
        this.edad = edad;
        this.telefono = telefono;
        this.correo = correo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
}

