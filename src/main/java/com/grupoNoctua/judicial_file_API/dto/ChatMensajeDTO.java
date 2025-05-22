package com.grupoNoctua.judicial_file_API.dto;

import java.time.LocalDateTime;

public class ChatMensajeDTO {

    private Long id;
    private String contenido;
    private LocalDateTime fechaEnvio;
    private Long emisorId;
    private String emisorUsername;
    private Long receptorId;
    private String receptorUsername;

    public ChatMensajeDTO() {
    }

    public ChatMensajeDTO(Long id, String contenido, LocalDateTime fechaEnvio,
                          Long emisorId, String emisorUsername,
                          Long receptorId, String receptorUsername) {
        this.id = id;
        this.contenido = contenido;
        this.fechaEnvio = fechaEnvio;
        this.emisorId = emisorId;
        this.emisorUsername = emisorUsername;
        this.receptorId = receptorId;
        this.receptorUsername = receptorUsername;
    }

    // Getters y setters completos

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public Long getEmisorId() {
        return emisorId;
    }

    public void setEmisorId(Long emisorId) {
        this.emisorId = emisorId;
    }

    public String getEmisorUsername() {
        return emisorUsername;
    }

    public void setEmisorUsername(String emisorUsername) {
        this.emisorUsername = emisorUsername;
    }

    public Long getReceptorId() {
        return receptorId;
    }

    public void setReceptorId(Long receptorId) {
        this.receptorId = receptorId;
    }

    public String getReceptorUsername() {
        return receptorUsername;
    }

    public void setReceptorUsername(String receptorUsername) {
        this.receptorUsername = receptorUsername;
    }
}
