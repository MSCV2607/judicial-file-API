package com.grupoNoctua.judicial_file_API.repository;

import com.grupoNoctua.judicial_file_API.entity.ChatMensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMensajeRepository extends JpaRepository<ChatMensaje, Long> {

    @Query("SELECT m FROM ChatMensaje m WHERE " +
            "(m.emisor.id = ?1 AND m.receptor.id = ?2) OR " +
            "(m.emisor.id = ?2 AND m.receptor.id = ?1) " +
            "ORDER BY m.fechaEnvio ASC")
    List<ChatMensaje> findMensajesEntreUsuarios(Long emisorId, Long receptorId);
}

