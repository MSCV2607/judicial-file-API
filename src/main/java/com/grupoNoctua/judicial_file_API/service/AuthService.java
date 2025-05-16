package com.grupoNoctua.judicial_file_API.service;

import com.grupoNoctua.judicial_file_API.dto.LoginRequest;
import com.grupoNoctua.judicial_file_API.dto.RegisterRequest;
import com.grupoNoctua.judicial_file_API.dto.JwtResponse;
import com.grupoNoctua.judicial_file_API.entity.Persona;
import com.grupoNoctua.judicial_file_API.entity.Usuario;
import com.grupoNoctua.judicial_file_API.exception.CustomException;
import com.grupoNoctua.judicial_file_API.repository.PersonaRepository;
import com.grupoNoctua.judicial_file_API.repository.UsuarioRepository;
import com.grupoNoctua.judicial_file_API.security.JwtService;
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

    @Autowired
    private JwtService jwtService;

    public String register(RegisterRequest request) {
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new CustomException("El nombre de usuario ya está en uso.", 400);
        }

        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new CustomException("El correo electrónico ya está en uso.", 400);
        }

        if (personaRepository.findByDni(request.getDni()).isPresent()) {
            throw new CustomException("Ya existe una persona registrada con ese DNI.", 400);
        }

        Persona persona = new Persona();
        persona.setNombre(request.getNombre());
        persona.setApellido(request.getApellido());
        persona.setDni(request.getDni());
        persona = personaRepository.save(persona);

        Usuario usuario = new Usuario();
        usuario.setPersona(persona);
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setEsAdmin(false);

        usuarioRepository.save(usuario);

        return "Usuario registrado correctamente.";
    }

    public JwtResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomException("Usuario no encontrado", 404));

        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), usuario.getPassword());

        if (!passwordMatches) {
            throw new CustomException("Contraseña incorrecta", 401);
        }

        String jwtToken = jwtService.generateToken(usuario.getUsername());
        return new JwtResponse(jwtToken);
    }
}
