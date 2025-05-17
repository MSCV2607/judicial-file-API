package com.grupoNoctua.judicial_file_API.service;

import com.grupoNoctua.judicial_file_API.entity.Expediente;
import com.grupoNoctua.judicial_file_API.entity.Usuario;
import com.grupoNoctua.judicial_file_API.repository.ExpedienteRepository;
import com.grupoNoctua.judicial_file_API.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExpedienteService {

    private static final String EXPEDIENTES_DIR = System.getProperty("user.dir") + "/EXPEDIENTES/";

    @Autowired
    private ExpedienteRepository expedienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public void crearExpediente(String dni, String nombre, String apellido, MultipartFile[] archivos) throws IOException {
        File carpetaRaiz = new File(EXPEDIENTES_DIR);
        if (!carpetaRaiz.exists()) carpetaRaiz.mkdir();

        File carpetaDNI = new File(EXPEDIENTES_DIR + dni);
        if (carpetaDNI.exists()) {
            throw new IllegalArgumentException("El expediente con DNI " + dni + " ya existe.");
        }

        if (!carpetaDNI.mkdir()) {
            throw new IOException("No se pudo crear la carpeta del expediente.");
        }

        for (MultipartFile archivo : archivos) {
            File nuevoArchivo = new File(carpetaDNI, archivo.getOriginalFilename());
            try (FileOutputStream fos = new FileOutputStream(nuevoArchivo)) {
                fos.write(archivo.getBytes());
            }
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario encargado = usuarioRepository.findByUsername(username).orElseThrow();

        Expediente expediente = new Expediente();
        expediente.setDni(dni);
        expediente.setNombre(nombre);
        expediente.setApellido(apellido);
        expediente.setUltimaActualizacion(LocalDateTime.now());
        expediente.setEncargado(encargado);

        expedienteRepository.save(expediente);
    }

    public List<Expediente> listarExpedientesDelUsuario() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        return expedienteRepository.findByEncargadoOrColaboradoresContaining(usuario, usuario);
    }
}

