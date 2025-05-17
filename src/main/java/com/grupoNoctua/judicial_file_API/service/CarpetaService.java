package com.grupoNoctua.judicial_file_API.service;

import com.grupoNoctua.judicial_file_API.entity.Carpeta;
import com.grupoNoctua.judicial_file_API.entity.Usuario;
import com.grupoNoctua.judicial_file_API.repository.CarpetaRepository;
import com.grupoNoctua.judicial_file_API.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CarpetaService {

    private static final String EXPEDIENTES_DIR = System.getProperty("user.dir") + "/EXPEDIENTES/";

    @Autowired
    private CarpetaRepository carpetaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public void crearCarpeta(String dni, String nombre, String apellido, MultipartFile[] archivos) throws IOException {
        // 1. Crear carpeta raíz si no existe
        File carpetaRaiz = new File(EXPEDIENTES_DIR);
        if (!carpetaRaiz.exists()) carpetaRaiz.mkdir();

        // 2. Validar si la carpeta (expediente) ya existe
        Optional<Carpeta> existente = carpetaRepository.findByNumeroCarpeta(dni);
        if (existente.isPresent()) {
            throw new IllegalArgumentException("La carpeta con DNI " + dni + " ya existe.");
        }

        // 3. Crear carpeta física
        File carpetaDNI = new File(EXPEDIENTES_DIR + dni);
        if (!carpetaDNI.mkdir()) {
            throw new IOException("No se pudo crear la carpeta física.");
        }

        // 4. Guardar archivos físicos
        for (MultipartFile archivo : archivos) {
            File nuevoArchivo = new File(carpetaDNI, archivo.getOriginalFilename());
            try (FileOutputStream fos = new FileOutputStream(nuevoArchivo)) {
                fos.write(archivo.getBytes());
            }
        }

        // 5. Obtener usuario autenticado desde el contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();  // Usuario logueado

        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuario autenticado no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        // 6. Crear la entidad Carpeta y asociar datos
        Carpeta carpeta = new Carpeta();
        carpeta.setNumeroCarpeta(dni);
        carpeta.setDescripcion(nombre + " " + apellido);
        carpeta.setFechaCreacion(LocalDate.now());
        carpeta.setUltimaActualizacion(LocalDateTime.now());
        carpeta.setDirectorio(carpetaDNI.getAbsolutePath());

        Set<Usuario> encargados = new HashSet<>();
        encargados.add(usuario);
        carpeta.setEncargados(encargados);

        carpetaRepository.save(carpeta);
    }

    public List<Carpeta> listarCarpetasPorUsuario(String username) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        return carpetaRepository.findAll().stream()
                .filter(c -> c.getEncargados().contains(usuario))
                .toList();
    }
}
