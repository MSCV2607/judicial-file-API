package com.grupoNoctua.judicial_file_API.dto;

public class ExpedienteDTO {
    private Long id;
    private String nombreCarpeta;
    private String nombre;
    private String apellido;
    private String encargado;
    private String ultimaActualizacion;
    private String edad;
    private String telefono;
    private String correo;

    public ExpedienteDTO(Long id, String nombreCarpeta, String nombre, String apellido, String encargado,
                         String ultimaActualizacion, String edad, String telefono, String correo) {
        this.id = id;
        this.nombreCarpeta = nombreCarpeta;
        this.nombre = nombre;
        this.apellido = apellido;
        this.encargado = encargado;
        this.ultimaActualizacion = ultimaActualizacion;
        this.edad = edad;
        this.telefono = telefono;
        this.correo = correo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreCarpeta() { return nombreCarpeta; }
    public void setNombreCarpeta(String nombreCarpeta) { this.nombreCarpeta = nombreCarpeta; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEncargado() { return encargado; }
    public void setEncargado(String encargado) { this.encargado = encargado; }

    public String getUltimaActualizacion() { return ultimaActualizacion; }
    public void setUltimaActualizacion(String ultimaActualizacion) { this.ultimaActualizacion = ultimaActualizacion; }

    public String getEdad() { return edad; }
    public void setEdad(String edad) { this.edad = edad; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
}


