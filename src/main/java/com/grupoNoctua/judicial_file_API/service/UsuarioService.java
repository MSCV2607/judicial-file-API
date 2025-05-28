package com.grupoNoctua.judicial_file_API.service;

import com.grupoNoctua.judicial_file_API.entity.Usuario;
import com.grupoNoctua.judicial_file_API.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario buscarPorUsername(String username) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        return usuarioOpt.orElse(null);
    }

    public Usuario buscarPorId(Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        return usuarioOpt.orElse(null);
    }


    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }
}

