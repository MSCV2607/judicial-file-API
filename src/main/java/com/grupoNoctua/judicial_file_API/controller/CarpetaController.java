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
            @RequestParam MultipartFile[] archivos) {
        try {
            carpetaService.crearCarpeta(dni, nombre, apellido, archivos);
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

    @GetMapping("/archivos/{dni}")
    public ResponseEntity<List<String>> verArchivosDeCarpeta(@PathVariable String dni) {
        try {
            List<String> archivos = carpetaService.listarArchivosPorDni(dni);
            return ResponseEntity.ok(archivos);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/descargar")
    public void descargarCarpeta(@RequestParam String dni, HttpServletResponse response) throws IOException {
        carpetaService.descargarCarpetaComoZip(dni, response);
    }

    @GetMapping("/archivo")
    public void descargarArchivoEspecifico(
            @RequestParam String dni,
            @RequestParam String nombreArchivo,
            HttpServletResponse response) throws IOException {
        carpetaService.descargarArchivoEspecifico(dni, nombreArchivo, response);
    }

    @PostMapping("/actualizar")
    public ResponseEntity<String> agregarArchivosACarpeta(
            @RequestParam String dni,
            @RequestParam MultipartFile[] archivos) {
        try {
            carpetaService.agregarArchivosACarpeta(dni, archivos);
            return ResponseEntity.ok("Archivos agregados correctamente");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al subir archivos");
        }
    }

    // eliminar archivo específico
    @DeleteMapping("/archivo")
    public ResponseEntity<String> eliminarArchivo(
            @RequestParam String dni,
            @RequestParam String nombreArchivo) {
        try {
            carpetaService.eliminarArchivoDeCarpeta(dni, nombreArchivo);
            return ResponseEntity.ok("Archivo eliminado correctamente");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error al eliminar archivo: " + e.getMessage());
        }
    }

    // Eliminar carpeta completa
    @DeleteMapping("/eliminar")
    public ResponseEntity<String> eliminarCarpetaCompleta(@RequestParam String dni) {
        try {
            carpetaService.eliminarCarpetaCompleta(dni);
            return ResponseEntity.ok("Carpeta eliminada correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error al eliminar la carpeta: " + e.getMessage());
        }
    }

}