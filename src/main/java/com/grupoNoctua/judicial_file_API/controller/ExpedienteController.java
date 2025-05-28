package com.grupoNoctua.judicial_file_API.controller;

import com.grupoNoctua.judicial_file_API.entity.Carpeta;
import com.grupoNoctua.judicial_file_API.service.ExpedienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/expedientes")
public class ExpedienteController {

    @Autowired
    private ExpedienteService expedienteService;

    @GetMapping("/listar")
    public ResponseEntity<List<Carpeta>> listarExpedientes(Authentication authentication) {
        String username = authentication.getName();

        List<Carpeta> carpetas = expedienteService.listarCarpetasPorUsuario(username);
        return ResponseEntity.ok(carpetas);
    }
}

