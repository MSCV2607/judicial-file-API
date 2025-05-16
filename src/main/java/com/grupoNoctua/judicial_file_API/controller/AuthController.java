package com.grupoNoctua.judicial_file_API.controller;

import com.grupoNoctua.judicial_file_API.dto.LoginRequest;
import com.grupoNoctua.judicial_file_API.dto.RegisterRequest;
import com.grupoNoctua.judicial_file_API.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        String mensaje = authService.register(request);
        return ResponseEntity.ok(mensaje);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String mensaje = authService.login(request);
        return ResponseEntity.ok(mensaje);
    }
}

