package com.grupoNoctua.judicial_file_API.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class ExpedienteService {

    private static final String EXPEDIENTES_DIR = System.getProperty("user.dir") + "/EXPEDIENTES/";

    public void crearExpediente(String dni, String nombre, String apellido, MultipartFile[] archivos) throws IOException {
        File carpetaRaiz = new File(EXPEDIENTES_DIR);
        if (!carpetaRaiz.exists()) carpetaRaiz.mkdir();

        File carpetaDNI = new File(EXPEDIENTES_DIR + dni);
        if (carpetaDNI.exists()) {
            throw new IllegalArgumentException("El expediente con DNI " + dni + " ya existe.");
        }

        if (!carpetaDNI.mkdir()) {
            throw new IOException("No se pudo crear la carpeta del expediente.");
        }

        for (MultipartFile archivo : archivos) {
            File nuevoArchivo = new File(carpetaDNI, archivo.getOriginalFilename());
            try (FileOutputStream fos = new FileOutputStream(nuevoArchivo)) {
                fos.write(archivo.getBytes());
            }
        }

        // TODO: Guardar info del expediente en la base de datos si es necesario
    }
}
