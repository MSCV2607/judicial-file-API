package com.grupoNoctua.judicial_file_API.controller;

import com.grupoNoctua.judicial_file_API.entity.Carpeta;
import com.grupoNoctua.judicial_file_API.service.CarpetaService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/carpetas")
@CrossOrigin(origins = "*")
public class CarpetaController {

    @Autowired
    private CarpetaService carpetaService;

    @PostMapping("/crear")
    public ResponseEntity<String> crearCarpeta(
            @RequestParam String dni,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String nombreCarpeta,
            @RequestParam(required = false) Integer edad,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String correo,
            @RequestParam MultipartFile[] archivos) {
        try {
            carpetaService.crearCarpeta(dni, nombre, apellido, nombreCarpeta, edad, telefono, correo, archivos);
            return ResponseEntity.ok("Carpeta creada con éxito");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error al guardar archivos");
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Carpeta>> listarCarpetasUsuarioAutenticado() {
        List<Carpeta> carpetas = carpetaService.listarCarpetasDelUsuarioAutenticado();
        return ResponseEntity.ok(carpetas);
    }

    @GetMapping("/archivos/{id}")
    public ResponseEntity<List<String>> verArchivosDeCarpeta(@PathVariable Long id) {
        try {
            List<String> archivos = carpetaService.listarArchivosPorId(id);
            return ResponseEntity.ok(archivos);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/descargar")
    public void descargarCarpeta(@RequestParam Long id, HttpServletResponse response) throws IOException {
        carpetaService.descargarCarpetaComoZip(id, response);
    }

    @GetMapping("/archivo")
    public void descargarArchivoEspecifico(
            @RequestParam Long id,
            @RequestParam String nombreArchivo,
            HttpServletResponse response) throws IOException {
        carpetaService.descargarArchivoEspecifico(id, nombreArchivo, response);
    }

    @PostMapping("/actualizar")
    public ResponseEntity<String> agregarArchivosACarpeta(
            @RequestParam Long id,
            @RequestParam MultipartFile[] archivos,
            @RequestParam String descripcion) {
        try {
            carpetaService.agregarArchivosACarpeta(id, archivos, descripcion);
            return ResponseEntity.ok("Archivos agregados correctamente");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al subir archivos");
        }
    }

    @DeleteMapping("/archivo")
    public ResponseEntity<String> eliminarArchivo(
            @RequestParam Long id,
            @RequestParam String nombreArchivo,
            @RequestParam String descripcion) {
        try {
            carpetaService.eliminarArchivoDeCarpeta(id, nombreArchivo, descripcion);
            return ResponseEntity.ok("Archivo eliminado correctamente");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error al eliminar archivo: " + e.getMessage());
        }
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<String> eliminarCarpetaCompleta(@RequestParam Long id) {
        try {
            carpetaService.eliminarCarpetaCompleta(id);
            return ResponseEntity.ok("Carpeta eliminada correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error al eliminar la carpeta: " + e.getMessage());
        }
    }

    @PostMapping("/unirse")
    public ResponseEntity<String> unirseACarpeta(@RequestParam Long id) {
        try {
            carpetaService.unirseACarpetaPorId(id);
            return ResponseEntity.ok("Te has unido correctamente a la carpeta");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Carpeta>> buscarCarpetas(@RequestParam String query) {
        List<Carpeta> resultados = carpetaService.buscarCarpetasPorTexto(query);
        return ResponseEntity.ok(resultados);
    }
}



