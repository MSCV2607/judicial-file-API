package com.grupoNoctua.judicial_file_API.service;

import com.grupoNoctua.judicial_file_API.entity.Carpeta;
import com.grupoNoctua.judicial_file_API.entity.Usuario;
import com.grupoNoctua.judicial_file_API.repository.CarpetaRepository;
import com.grupoNoctua.judicial_file_API.repository.UsuarioRepository;
import com.grupoNoctua.judicial_file_API.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
        carpeta.setEstado("ACTUALIZACIÓN RECIENTE");
        carpeta.setDescripcionUltimaActualizacion("Carpeta creada con " + archivos.length + " archivo(s).");
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
            throw new IOException("Carpeta no encontrada");
        }

        File zipTemporal = File.createTempFile("expediente_" + dni + "_", ".zip");

        try (FileOutputStream fos = new FileOutputStream(zipTemporal);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(bos)) {

            File[] archivos = carpeta.listFiles();
            if (archivos != null) {
                for (File archivo : archivos) {
                    zos.putNextEntry(new java.util.zip.ZipEntry(archivo.getName()));
                    FileInputStream fis = new FileInputStream(archivo);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) >= 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                    fis.close();
                }
            }
        }

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=" + dni + ".zip");

        try (InputStream is = new FileInputStream(zipTemporal)) {
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        }

        zipTemporal.delete();
    }

    public void descargarArchivoEspecifico(String dni, String nombreArchivo, HttpServletResponse response) throws IOException {
        File archivo = new File(EXPEDIENTES_DIR + dni + "/" + nombreArchivo);
        if (!archivo.exists() || !archivo.isFile()) {
            throw new IOException("El archivo no existe");
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + archivo.getName() + "\"");

        try (InputStream is = new FileInputStream(archivo)) {
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        }
    }

    public void agregarArchivosACarpeta(String dni, MultipartFile[] archivos, String descripcion) throws IOException {
        Optional<Carpeta> carpetaOpt = carpetaRepository.findByNumeroCarpeta(dni);
        if (carpetaOpt.isEmpty()) {
            throw new IllegalArgumentException("No existe una carpeta con ese DNI");
        }

        File carpetaDni = new File(EXPEDIENTES_DIR + dni);
        if (!carpetaDni.exists()) {
            throw new IOException("La carpeta física no existe en el servidor");
        }

        for (MultipartFile archivo : archivos) {
            File nuevoArchivo = new File(carpetaDni, archivo.getOriginalFilename());
            try (FileOutputStream fos = new FileOutputStream(nuevoArchivo)) {
                fos.write(archivo.getBytes());
            }
        }

        Carpeta carpeta = carpetaOpt.get();
        carpeta.setUltimaActualizacion(LocalDateTime.now());
        carpeta.setEstado(calcularEstado(carpeta.getUltimaActualizacion()));
        carpeta.setDescripcionUltimaActualizacion(descripcion);
        carpetaRepository.save(carpeta);
    }

    public void eliminarArchivoDeCarpeta(String dni, String nombreArchivo, String descripcion) throws IOException {
        File archivo = new File(EXPEDIENTES_DIR + dni + "/" + nombreArchivo);
        if (!archivo.exists() || !archivo.isFile()) {
            throw new IOException("El archivo no existe");
        }

        if (!archivo.delete()) {
            throw new IOException("No se pudo eliminar el archivo");
        }

        Optional<Carpeta> carpetaOpt = carpetaRepository.findByNumeroCarpeta(dni);
        if (carpetaOpt.isPresent()) {
            Carpeta carpeta = carpetaOpt.get();
            carpeta.setUltimaActualizacion(LocalDateTime.now());
            carpeta.setEstado(calcularEstado(carpeta.getUltimaActualizacion()));
            carpeta.setDescripcionUltimaActualizacion(descripcion);
            carpetaRepository.save(carpeta);
        }
    }

    public void eliminarCarpetaCompleta(String dni) throws IOException {
        Optional<Carpeta> carpetaOpt = carpetaRepository.findByNumeroCarpeta(dni);
        if (carpetaOpt.isEmpty()) {
            throw new IllegalArgumentException("No existe una carpeta con ese DNI");
        }

        File carpetaDni = new File(EXPEDIENTES_DIR + dni);
        if (carpetaDni.exists()) {
            eliminarRecursivamente(carpetaDni);
        }

        carpetaRepository.delete(carpetaOpt.get());
    }

    private void eliminarRecursivamente(File file) {
        if (file.isDirectory()) {
            for (File sub : Objects.requireNonNull(file.listFiles())) {
                eliminarRecursivamente(sub);
            }
        }
        file.delete();
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

    private String calcularEstado(LocalDateTime ultimaActualizacion) {
        long dias = ChronoUnit.DAYS.between(ultimaActualizacion.toLocalDate(), LocalDate.now());
        if (dias <= 10) return "ACTUALIZACIÓN RECIENTE";
        if (dias <= 20) return "ACTUALIZACIÓN POCO RECIENTE";
        return "HACE MUCHO NO SE ACTUALIZA";
    }

    public List<Carpeta> buscarCarpetasPorTexto(String query) {
        String username = obtenerUsernameActual();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        Usuario usuario = usuarioOpt.get();

        String texto = query.toLowerCase();

        return carpetaRepository.findAll().stream()
                .filter(c -> c.getEncargados().contains(usuario))
                .filter(c ->
                        c.getNumeroCarpeta().toLowerCase().contains(texto) ||
                                c.getDescripcion().toLowerCase().contains(texto)
                )
                .toList();
    }

}



