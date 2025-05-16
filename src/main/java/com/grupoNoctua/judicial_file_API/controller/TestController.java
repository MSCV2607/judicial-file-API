package com.grupoNoctua.judicial_file_API.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/protegido")
public class TestController {

    @GetMapping
    public String accesoSeguro() {
        return "✅ Accediste a una ruta protegida con JWT";
    }
}

