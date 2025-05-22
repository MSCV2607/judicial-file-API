package com.grupoNoctua.judicial_file_API.controller;

import com.grupoNoctua.judicial_file_API.dto.UsuarioDTO;
import com.grupoNoctua.judicial_file_API.entity.Usuario;
import com.grupoNoctua.judicial_file_API.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarTodos();

        List<UsuarioDTO> usuarioDTOs = usuarios.stream().map(u -> {
            String nombreCompleto = "";
            if (u.getPersona() != null) {
                String nombre = u.getPersona().getNombre() != null ? u.getPersona().getNombre() : "";
                String apellido = u.getPersona().getApellido() != null ? u.getPersona().getApellido() : "";
                nombreCompleto = nombre + " " + apellido;
            }
            return new UsuarioDTO(u.getId(), u.getUsername(), nombreCompleto.trim());
        }).collect(Collectors.toList());

        return ResponseEntity.ok(usuarioDTOs);
    }
}
