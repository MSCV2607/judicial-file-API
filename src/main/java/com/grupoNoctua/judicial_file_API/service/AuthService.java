package com.grupoNoctua.judicial_file_API.service;


import com.grupoNoctua.judicial_file_API.dto.RegisterRequest;
import com.grupoNoctua.judicial_file_API.entity.Persona;
import com.grupoNoctua.judicial_file_API.entity.Usuario;
import com.grupoNoctua.judicial_file_API.repository.PersonaRepository;
import com.grupoNoctua.judicial_file_API.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String register(RegisterRequest request) {
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya está en uso.");
        }

        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("El correo electrónico ya está en uso.");
        }

        if (personaRepository.findByDni(request.getDni()).isPresent()) {
            throw new RuntimeException("Ya existe una persona registrada con ese DNI.");
        }

        // Guardamos persona
        Persona persona = new Persona();
        persona.setNombre(request.getNombre());
        persona.setApellido(request.getApellido());
        persona.setDni(request.getDni());
        persona = personaRepository.save(persona);

        // Creamos usuario vinculado
        Usuario usuario = new Usuario();
        usuario.setPersona(persona);
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setEsAdmin(false);

        usuarioRepository.save(usuario);

        return "Usuario registrado correctamente.";
    }

}
