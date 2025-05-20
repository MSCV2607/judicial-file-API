package com.grupoNoctua.judicial_file_API.controller;

import com.grupoNoctua.judicial_file_API.entity.Carpeta;
import com.grupoNoctua.judicial_file_API.service.CarpetaService;
import jakarta.servlet.http.HttpServletResponse;
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

    @GetMapping("/descargar-archivo")
    public void descargarArchivo(@RequestParam String dni, @RequestParam String nombre, HttpServletResponse response) throws IOException {
        carpetaService.descargarArchivoEspecifico(dni, nombre, response);
    }

    @PutMapping("/actualizar")
    public ResponseEntity<String> agregarArchivos(
            @RequestParam String dni,
            @RequestParam MultipartFile[] archivos) {
        try {
            carpetaService.agregarArchivosACarpeta(dni, archivos);
            return ResponseEntity.ok("Archivos agregados correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error al guardar archivos");
        }
    }

}



