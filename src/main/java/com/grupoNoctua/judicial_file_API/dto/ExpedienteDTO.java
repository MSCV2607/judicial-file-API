package com.grupoNoctua.judicial_file_API.dto;

public class ExpedienteDTO {
    private String dni;
    private String nombre;
    private String apellido;
    private String encargado;
    private String ultimaActualizacion;

    // Constructor
    public ExpedienteDTO(String dni, String nombre, String apellido, String encargado, String ultimaActualizacion) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.encargado = encargado;
        this.ultimaActualizacion = ultimaActualizacion;
    }

    // Getters y Setters
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEncargado() { return encargado; }
    public void setEncargado(String encargado) { this.encargado = encargado; }

    public String getUltimaActualizacion() { return ultimaActualizacion; }
    public void setUltimaActualizacion(String ultimaActualizacion) { this.ultimaActualizacion = ultimaActualizacion; }
}
