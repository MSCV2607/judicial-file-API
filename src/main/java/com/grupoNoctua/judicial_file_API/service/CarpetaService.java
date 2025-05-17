package com.grupoNoctua.judicial_file_API.service;

import com.grupoNoctua.judicial_file_API.entity.Carpeta;
import com.grupoNoctua.judicial_file_API.entity.Usuario;
import com.grupoNoctua.judicial_file_API.repository.CarpetaRepository;
import com.grupoNoctua.judicial_file_API.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CarpetaService {

    private static final String EXPEDIENTES_DIR = System.getProperty("user.dir") + "/EXPEDIENTES/";

    @Autowired
    private CarpetaRepository carpetaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ✅ Nuevo método con firma correcta
    public void crearCarpeta(String dni, String nombre, String apellido, MultipartFile[] archivos, String username) throws IOException {
        // Verificar carpeta raíz
        File carpetaRaiz = new File(EXPEDIENTES_DIR);
        if (!carpetaRaiz.exists()) carpetaRaiz.mkdir();

        // Verificar si carpeta ya existe en BD
        Optional<Carpeta> existente = carpetaRepository.findByNumeroCarpeta(dni);
        if (existente.isPresent()) {
            throw new IllegalArgumentException("La carpeta con DNI " + dni + " ya existe.");
        }

        // Crear carpeta física en disco
        File carpetaDNI = new File(EXPEDIENTES_DIR + dni);
        if (!carpetaDNI.mkdir()) {
            throw new IOException("No se pudo crear la carpeta física.");
        }

        // Guardar archivos físicos
        for (MultipartFile archivo : archivos) {
            File nuevoArchivo = new File(carpetaDNI, archivo.getOriginalFilename());
            try (FileOutputStream fos = new FileOutputStream(nuevoArchivo)) {
                fos.write(archivo.getBytes());
            }
        }

        // Crear la entidad Carpeta
        Carpeta carpeta = new Carpeta();
        carpeta.setNumeroCarpeta(dni);
        carpeta.setDescripcion(nombre + " " + apellido);
        carpeta.setFechaCreacion(LocalDate.now());
        carpeta.setUltimaActualizacion(LocalDateTime.now());
        carpeta.setDirectorio(carpetaDNI.getAbsolutePath());

        // Buscar usuario autenticado y asignar como encargado
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuario autenticado no encontrado.");
        }

        Set<Usuario> encargados = new HashSet<>();
        encargados.add(usuarioOpt.get());
        carpeta.setEncargados(encargados);

        carpetaRepository.save(carpeta);
    }

    // ✅ Método para listar carpetas del usuario autenticado
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

