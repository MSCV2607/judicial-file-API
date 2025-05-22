package com.grupoNoctua.judicial_file_API.controller;

import com.grupoNoctua.judicial_file_API.dto.ChatMensajeDTO;
import com.grupoNoctua.judicial_file_API.service.ChatMensajeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chatmensajes")
public class ChatMensajeController {

    private final ChatMensajeService chatMensajeService;

    public ChatMensajeController(ChatMensajeService chatMensajeService) {
        this.chatMensajeService = chatMensajeService;
    }

    // Listar mensajes entre dos usuarios (se pasan IDs en query params)
    @GetMapping("/conversacion")
    public ResponseEntity<List<ChatMensajeDTO>> listarMensajesEntreUsuarios(
            @RequestParam Long emisorId,
            @RequestParam Long receptorId) {

        List<ChatMensajeDTO> mensajes = chatMensajeService.listarMensajesEntreUsuarios(emisorId, receptorId);
        return ResponseEntity.ok(mensajes);
    }

    // Enviar un mensaje nuevo
    @PostMapping
    public ResponseEntity<ChatMensajeDTO> enviarMensaje(@RequestBody ChatMensajeDTO mensajeDTO) {
        ChatMensajeDTO mensajeGuardado = chatMensajeService.guardarMensaje(mensajeDTO);
        return ResponseEntity.ok(mensajeGuardado);
    }
}


