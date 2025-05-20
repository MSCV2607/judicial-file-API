package com.grupoNoctua.judicial_file_API.service;

import com.grupoNoctua.judicial_file_API.entity.Carpeta;
import com.grupoNoctua.judicial_file_API.entity.Usuario;
import com.grupoNoctua.judicial_file_API.repository.CarpetaRepository;
import com.grupoNoctua.judicial_file_API.repository.UsuarioRepository;
import com.grupoNoctua.judicial_file_API.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class CarpetaService {

    private static final String EXPEDIENTES_DIR = System.getProperty("user.dir") + "/EXPEDIENTES/";

    @Autowired
    private CarpetaRepository carpetaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtService jwtService;

    public void crearCarpeta(String dni, String nombre, String apellido, MultipartFile[] archivos) throws IOException {
        File carpetaRaiz = new File(EXPEDIENTES_DIR);
        if (!carpetaRaiz.exists()) carpetaRaiz.mkdir();

        Optional<Carpeta> existente = carpetaRepository.findByNumeroCarpeta(dni);
        if (existente.isPresent()) {
            throw new IllegalArgumentException("La carpeta con DNI " + dni + " ya existe.");
        }

        File carpetaDNI = new File(EXPEDIENTES_DIR + dni);
        if (!carpetaDNI.mkdir()) {
            throw new IOException("No se pudo crear la carpeta física.");
        }

        for (MultipartFile archivo : archivos) {
            File nuevoArchivo = new File(carpetaDNI, archivo.getOriginalFilename());
            try (FileOutputStream fos = new FileOutputStream(nuevoArchivo)) {
                fos.write(archivo.getBytes());
            }
        }

        Carpeta carpeta = new Carpeta();
        carpeta.setNumeroCarpeta(dni);
        carpeta.setDescripcion(nombre + " " + apellido);
        carpeta.setFechaCreacion(LocalDate.now());
        carpeta.setUltimaActualizacion(LocalDateTime.now());
        carpeta.setDirectorio(carpetaDNI.getAbsolutePath());

        String username = obtenerUsernameActual();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuario autenticado no encontrado");
        }

        Set<Usuario> encargados = new HashSet<>();
        encargados.add(usuarioOpt.get());
        carpeta.setEncargados(encargados);

        carpetaRepository.save(carpeta);
    }

    public List<Carpeta> listarCarpetasDelUsuarioAutenticado() {
        String username = obtenerUsernameActual();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        return carpetaRepository.findAll().stream()
                .filter(c -> c.getEncargados().contains(usuario))
                .toList();
    }

    public List<String> listarArchivosPorDni(String dni) throws IOException {
        File carpetaDni = new File(EXPEDIENTES_DIR + dni);
        if (!carpetaDni.exists() || !carpetaDni.isDirectory()) {
            throw new IOException("Carpeta no encontrada en el servidor.");
        }

        String[] archivos = carpetaDni.list();
        if (archivos == null) return List.of();
        return Arrays.asList(archivos);
    }

    public void descargarCarpetaComoZip(String dni, HttpServletResponse response) throws IOException {
        File carpeta = new File(EXPEDIENTES_DIR + dni);
        if (!carpeta.exists() || !carpeta.isDirectory()) {
            throw new IllegalArgumentException("La carpeta no existe.");
        }

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=" + dni + ".zip");

        try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
            File[] archivos = carpeta.listFiles();
            if (archivos != null) {
                for (File archivo : archivos) {
                    try (FileInputStream fis = new FileInputStream(archivo)) {
                        ZipEntry zipEntry = new ZipEntry(archivo.getName());
                        zipOut.putNextEntry(zipEntry);

                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = fis.read(buffer)) >= 0) {
                            zipOut.write(buffer, 0, length);
                        }
                    }
                }
            }
            zipOut.finish();
        }
    }

    private String obtenerUsernameActual() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attrs.getRequest();
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalStateException("Token no presente");
        }
        String token = authHeader.substring(7);
        return jwtService.extractUsername(token);
    }
}


