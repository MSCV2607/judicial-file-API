package com.grupoNoctua.judicial_file_API.controller;

import com.grupoNoctua.judicial_file_API.service.ExpedienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/expedientes")
@CrossOrigin(origins = "*")
public class ExpedienteController {

    @Autowired
    private ExpedienteService expedienteService;

    @PostMapping("/crear")
    public ResponseEntity<String> crearExpediente(
            @RequestParam String dni,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam MultipartFile[] archivos) {
        try {
            expedienteService.crearExpediente(dni, nombre, apellido, archivos);
            return ResponseEntity.ok("Expediente creado con éxito");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error al guardar archivos");
        }
    }
}
