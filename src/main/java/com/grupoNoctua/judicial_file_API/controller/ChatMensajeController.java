package com.grupoNoctua.judicial_file_API.controller;

import com.grupoNoctua.judicial_file_API.dto.ChatMensajeDTO;
import com.grupoNoctua.judicial_file_API.service.ChatMensajeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@RestController
@RequestMapping("/api/chatmensajes")
public class ChatMensajeController {

    private final ChatMensajeService chatMensajeService;

    public ChatMensajeController(ChatMensajeService chatMensajeService) {
        this.chatMensajeService = chatMensajeService;
    }


    @GetMapping("/conversacion")
    public ResponseEntity<List<ChatMensajeDTO>> listarMensajesEntreUsuarios(
            @RequestParam Long emisorId,
            @RequestParam Long receptorId) {

        List<ChatMensajeDTO> mensajes = chatMensajeService.listarMensajesEntreUsuarios(emisorId, receptorId);
        return ResponseEntity.ok(mensajes);
    }


    @PostMapping
    public ResponseEntity<ChatMensajeDTO> enviarMensaje(
            @RequestBody ChatMensajeDTO mensajeDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername(); // <- se obtiene del token

        ChatMensajeDTO mensajeGuardado = chatMensajeService.guardarMensajeDesdeUsername(mensajeDTO, username);
        return ResponseEntity.ok(mensajeGuardado);
    }
}


