package com.grupoNoctua.judicial_file_API.service;

import com.grupoNoctua.judicial_file_API.entity.Carpeta;
import com.grupoNoctua.judicial_file_API.entity.Cliente;
import com.grupoNoctua.judicial_file_API.entity.Persona;
import com.grupoNoctua.judicial_file_API.entity.Usuario;
import com.grupoNoctua.judicial_file_API.repository.CarpetaRepository;
import com.grupoNoctua.judicial_file_API.repository.ClienteRepository;
import com.grupoNoctua.judicial_file_API.repository.PersonaRepository;
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

    @Autowired private CarpetaRepository carpetaRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private PersonaRepository personaRepository;
    @Autowired private JwtService jwtService;

    public void crearCarpeta(String dni, String nombre, String apellido, String nombreCarpeta, int edad, String telefono, String correo, MultipartFile[] archivos) throws IOException {
        File carpetaRaiz = new File(EXPEDIENTES_DIR);
        if (!carpetaRaiz.exists()) carpetaRaiz.mkdir();

        Persona persona = personaRepository.findByDni(dni).orElseGet(() -> {
            Persona p = new Persona();
            p.setDni(dni);
            p.setNombre(nombre);
            p.setApellido(apellido);
            return personaRepository.save(p);
        });

        Cliente cliente = clienteRepository.findById(persona.getId()).orElse(null);
        if (cliente == null) {
            cliente = new Cliente();
            cliente.setPersona(persona);
            cliente.setEdad(edad);
            cliente.setTelefono((telefono == null || telefono.isEmpty()) ? "N/A" : telefono);
            cliente.setCorreo((correo == null || correo.isEmpty()) ? "N/A" : correo);
            cliente = clienteRepository.save(cliente);
        }

        Carpeta carpeta = new Carpeta();
        carpeta.setNombreCarpeta(nombreCarpeta);
        carpeta.setDescripcion(nombre + " " + apellido);
        carpeta.setFechaCreacion(LocalDate.now());
        carpeta.setUltimaActualizacion(LocalDateTime.now());
        carpeta.setEstado("ACTUALIZACIÓN RECIENTE");
        carpeta.setDescripcionUltimaActualizacion("Carpeta creada con " + archivos.length + " archivo(s).");

        String username = obtenerUsernameActual();
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario autenticado no encontrado"));

        Set<Usuario> encargados = new HashSet<>();
        encargados.add(usuario);
        carpeta.setEncargados(encargados);

        Set<Cliente> clientes = new HashSet<>();
        clientes.add(cliente);
        carpeta.setClientes(clientes);

        carpeta = carpetaRepository.save(carpeta);

        String nombreDirectorio = carpeta.getId() + "-" + carpeta.getNombreCarpeta().replaceAll("\\s+", "_");
        File carpetaFisica = new File(EXPEDIENTES_DIR + nombreDirectorio);
        if (!carpetaFisica.mkdir()) {
            throw new IOException("No se pudo crear la carpeta física.");
        }

        for (MultipartFile archivo : archivos) {
            File nuevoArchivo = new File(carpetaFisica, archivo.getOriginalFilename());
            try (FileOutputStream fos = new FileOutputStream(nuevoArchivo)) {
                fos.write(archivo.getBytes());
            }
        }

        carpeta.setDirectorio(carpetaFisica.getAbsolutePath());
        carpetaRepository.save(carpeta);
    }

    public List<Carpeta> listarCarpetasDelUsuarioAutenticado() {
        String username = obtenerUsernameActual();
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        return carpetaRepository.findAll().stream()
                .filter(c -> c.getEncargados().contains(usuario))
                .toList();
    }

    public List<String> listarArchivosPorId(Long id) throws IOException {
        Carpeta carpeta = carpetaRepository.findById(id)
                .orElseThrow(() -> new IOException("Carpeta no encontrada"));
        File carpetaDir = new File(carpeta.getDirectorio());
        if (!carpetaDir.exists() || !carpetaDir.isDirectory()) {
            throw new IOException("Carpeta física no encontrada en el servidor.");
        }
        String[] archivos = carpetaDir.list();
        return archivos == null ? List.of() : Arrays.asList(archivos);
    }

    public void descargarCarpetaComoZip(Long id, HttpServletResponse response) throws IOException {
        Carpeta carpeta = carpetaRepository.findById(id)
                .orElseThrow(() -> new IOException("Carpeta no encontrada"));
        File carpetaDir = new File(carpeta.getDirectorio());
        File zipTemporal = File.createTempFile("expediente_" + id + "_", ".zip");

        try (FileOutputStream fos = new FileOutputStream(zipTemporal);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(bos)) {

            File[] archivos = carpetaDir.listFiles();
            if (archivos != null) {
                for (File archivo : archivos) {
                    zos.putNextEntry(new java.util.zip.ZipEntry(archivo.getName()));
                    try (FileInputStream fis = new FileInputStream(archivo)) {
                        IOUtils.copy(fis, zos);
                    }
                    zos.closeEntry();
                }
            }
        }

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=carpeta_" + id + ".zip");
        try (InputStream is = new FileInputStream(zipTemporal)) {
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        }
        zipTemporal.delete();
    }

    public void descargarArchivoEspecifico(Long id, String nombreArchivo, HttpServletResponse response) throws IOException {
        Carpeta carpeta = carpetaRepository.findById(id)
                .orElseThrow(() -> new IOException("Carpeta no encontrada"));
        File archivo = new File(carpeta.getDirectorio() + "/" + nombreArchivo);
        if (!archivo.exists()) throw new IOException("El archivo no existe");

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + archivo.getName() + "\"");

        try (InputStream is = new FileInputStream(archivo)) {
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        }
    }

    public void agregarArchivosACarpeta(Long id, MultipartFile[] archivos, String descripcion) throws IOException {
        Carpeta carpeta = carpetaRepository.findById(id)
                .orElseThrow(() -> new IOException("Carpeta no encontrada"));

        File carpetaDir = new File(carpeta.getDirectorio());
        if (!carpetaDir.exists()) throw new IOException("La carpeta física no existe en el servidor");

        for (MultipartFile archivo : archivos) {
            File nuevoArchivo = new File(carpetaDir, archivo.getOriginalFilename());
            try (FileOutputStream fos = new FileOutputStream(nuevoArchivo)) {
                fos.write(archivo.getBytes());
            }
        }

        carpeta.setUltimaActualizacion(LocalDateTime.now());
        carpeta.setEstado(calcularEstado(carpeta.getUltimaActualizacion()));
        carpeta.setDescripcionUltimaActualizacion(descripcion);
        carpetaRepository.save(carpeta);
    }

    public void eliminarArchivoDeCarpeta(Long id, String nombreArchivo, String descripcion) throws IOException {
        Carpeta carpeta = carpetaRepository.findById(id)
                .orElseThrow(() -> new IOException("Carpeta no encontrada"));
        File archivo = new File(carpeta.getDirectorio() + "/" + nombreArchivo);
        if (!archivo.exists() || !archivo.isFile()) throw new IOException("El archivo no existe");
        if (!archivo.delete()) throw new IOException("No se pudo eliminar el archivo");

        carpeta.setUltimaActualizacion(LocalDateTime.now());
        carpeta.setEstado(calcularEstado(carpeta.getUltimaActualizacion()));
        carpeta.setDescripcionUltimaActualizacion(descripcion);
        carpetaRepository.save(carpeta);
    }

    public void eliminarCarpetaCompleta(Long id) throws IOException {
        Carpeta carpeta = carpetaRepository.findById(id)
                .orElseThrow(() -> new IOException("Carpeta no encontrada"));
        File carpetaDir = new File(carpeta.getDirectorio());
        if (carpetaDir.exists()) eliminarRecursivamente(carpetaDir);
        carpetaRepository.delete(carpeta);
    }

    public void unirseACarpetaPorId(Long id) {
        Carpeta carpeta = carpetaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Carpeta no encontrada"));
        String username = obtenerUsernameActual();
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario autenticado no encontrado"));

        if (!carpeta.getEncargados().contains(usuario)) {
            carpeta.getEncargados().add(usuario);
            carpetaRepository.save(carpeta);
        }
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
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        String texto = query.toLowerCase();
        return carpetaRepository.findAll().stream()
                .filter(c -> c.getEncargados().contains(usuario))
                .filter(c ->
                        (c.getDescripcion() != null && c.getDescripcion().toLowerCase().contains(texto)) ||
                                (c.getNumeroCarpeta() != null && c.getNumeroCarpeta().toLowerCase().contains(texto)) ||
                                (c.getNombreCarpeta() != null && c.getNombreCarpeta().toLowerCase().contains(texto)) ||
                                c.getClientes().stream()
                                        .map(cliente -> personaRepository.findById(cliente.getId()).orElse(null))
                                        .filter(Objects::nonNull)
                                        .anyMatch(persona -> persona.getDni() != null && persona.getDni().contains(texto))
                )
                .toList();
    }
}
