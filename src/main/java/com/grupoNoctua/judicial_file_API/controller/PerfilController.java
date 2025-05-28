package com.grupoNoctua.judicial_file_API.controller;

import com.grupoNoctua.judicial_file_API.dto.PerfilUsuarioDTO;
import com.grupoNoctua.judicial_file_API.entity.Usuario;
import com.grupoNoctua.judicial_file_API.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;

@RestController
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<PerfilUsuarioDTO> obtenerPerfil() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<Usuario> usuarioOptional = usuarioRepository.findByUsername(username);
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = usuarioOptional.get();

        PerfilUsuarioDTO dto = new PerfilUsuarioDTO();
        dto.setNombre(usuario.getPersona().getNombre());
        dto.setApellido(usuario.getPersona().getApellido());
        dto.setUsername(usuario.getUsername());
        dto.setEmail(usuario.getEmail());
        dto.setFotoPerfil(usuario.getFotoPerfil());

        return ResponseEntity.ok(dto);
    }

    @PutMapping
    public ResponseEntity<?> actualizarPerfil(@RequestBody PerfilUsuarioDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<Usuario> usuarioOptional = usuarioRepository.findByUsername(username);
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = usuarioOptional.get();
        usuario.getPersona().setNombre(dto.getNombre());
        usuario.getPersona().setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());

        usuarioRepository.save(usuario);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/foto")
    public ResponseEntity<?> subirFoto(@RequestParam("foto") MultipartFile archivo) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<Usuario> usuarioOptional = usuarioRepository.findByUsername(username);
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = usuarioOptional.get();

        try {
            String extension = archivo.getOriginalFilename().substring(archivo.getOriginalFilename().lastIndexOf('.'));
            Path carpeta = Paths.get("FOTOPERFIL");
            if (!Files.exists(carpeta)) {
                Files.createDirectories(carpeta);
            }

            Path destino = carpeta.resolve(username + extension);
            Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

            usuario.setFotoPerfil(username + extension);
            usuarioRepository.save(usuario);

            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error al guardar la foto");
        }
    }

    @GetMapping("/foto")
    public ResponseEntity<Resource> obtenerFoto() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<Usuario> usuarioOptional = usuarioRepository.findByUsername(username);
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = usuarioOptional.get();
        Path carpeta = Paths.get("FOTOPERFIL");
        Path archivoFoto;

        try {

            if (usuario.getFotoPerfil() != null) {
                archivoFoto = carpeta.resolve(usuario.getFotoPerfil());
                if (Files.exists(archivoFoto)) {
                    Resource recurso = new UrlResource(archivoFoto.toUri());
                    return ResponseEntity.ok()
                            .contentType(Files.probeContentType(archivoFoto) != null
                                    ? MediaType.parseMediaType(Files.probeContentType(archivoFoto))
                                    : MediaType.APPLICATION_OCTET_STREAM)
                            .body(recurso);
                }
            }


            archivoFoto = carpeta.resolve("FOTODEPERFILDEFECTO.jpg");
            if (Files.exists(archivoFoto)) {
                Resource recurso = new UrlResource(archivoFoto.toUri());
                return ResponseEntity.ok()
                        .contentType(Files.probeContentType(archivoFoto) != null
                                ? MediaType.parseMediaType(Files.probeContentType(archivoFoto))
                                : MediaType.APPLICATION_OCTET_STREAM)
                        .body(recurso);
            }

            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

