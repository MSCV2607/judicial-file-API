package com.grupoNoctua.judicial_file_API.service;

import com.grupoNoctua.judicial_file_API.dto.ChatMensajeDTO;
import com.grupoNoctua.judicial_file_API.entity.ChatMensaje;
import com.grupoNoctua.judicial_file_API.entity.Usuario;
import com.grupoNoctua.judicial_file_API.repository.ChatMensajeRepository;
import com.grupoNoctua.judicial_file_API.service.UsuarioService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatMensajeService {

    private final ChatMensajeRepository chatMensajeRepository;
    private final UsuarioService usuarioService;

    public ChatMensajeService(ChatMensajeRepository chatMensajeRepository, UsuarioService usuarioService) {
        this.chatMensajeRepository = chatMensajeRepository;
        this.usuarioService = usuarioService;
    }

    // Conversión Entity -> DTO
    public ChatMensajeDTO convertirADTO(ChatMensaje mensaje) {
        return new ChatMensajeDTO(
                mensaje.getId(),
                mensaje.getContenido(),
                mensaje.getFechaEnvio(),
                mensaje.getEmisor().getId(),
                mensaje.getEmisor().getUsername(),
                mensaje.getReceptor().getId(),
                mensaje.getReceptor().getUsername()
        );
    }

    // Listar mensajes entre dos usuarios
    public List<ChatMensajeDTO> listarMensajesEntreUsuarios(Long emisorId, Long receptorId) {
        List<ChatMensaje> mensajes = chatMensajeRepository.findMensajesEntreUsuarios(emisorId, receptorId);
        return mensajes.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    // Guardar mensaje a partir de DTO (se asume que DTO tiene emisorId y receptorId válidos)
    public ChatMensajeDTO guardarMensajeDesdeUsername(ChatMensajeDTO dto, String emisorUsername) {
        Usuario emisor = usuarioService.buscarPorUsername(emisorUsername);
        Usuario receptor = usuarioService.buscarPorId(dto.getReceptorId());

        ChatMensaje mensaje = new ChatMensaje();
        mensaje.setContenido(dto.getContenido());
        mensaje.setFechaEnvio(LocalDateTime.now());
        mensaje.setEmisor(emisor);
        mensaje.setReceptor(receptor);

        ChatMensaje guardado = chatMensajeRepository.save(mensaje);
        return convertirADTO(guardado);
    }
}


