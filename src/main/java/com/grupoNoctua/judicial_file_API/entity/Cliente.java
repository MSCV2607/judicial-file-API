package com.grupoNoctua.judicial_file_API.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    private Long id;  // Es el mismo id que en persona

    private Integer edad;

    // Constructor vacío (requerido por JPA)
    public Cliente() {}

    // Constructor con campos
    public Cliente(Long id, Integer edad) {
        this.id = id;
        this.edad = edad;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }
}
