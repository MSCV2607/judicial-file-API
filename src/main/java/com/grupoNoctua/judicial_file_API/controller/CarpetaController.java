package com.grupoNoctua.judicial_file_API.controller;

import com.grupoNoctua.judicial_file_API.entity.Carpeta;
import com.grupoNoctua.judicial_file_API.service.CarpetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
            @RequestParam MultipartFile[] archivos,
            Authentication auth // automáticamente inyectado desde el token
    ) {
        try {
            String username = auth.getName(); // obtenemos el username desde el token
            carpetaService.crearCarpeta(dni, nombre, apellido, archivos, username);
            return ResponseEntity.ok("Carpeta creada con éxito");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error al guardar archivos");
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Carpeta>> listarCarpetasDelUsuarioAutenticado(Authentication auth) {
        try {
            String username = auth.getName(); // obtenemos el username del token
            List<Carpeta> carpetas = carpetaService.listarCarpetasPorUsuario(username);
            return ResponseEntity.ok(carpetas);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}


