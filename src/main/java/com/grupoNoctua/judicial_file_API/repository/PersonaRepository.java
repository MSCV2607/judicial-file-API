package com.grupoNoctua.judicial_file_API.repository;

import com.grupoNoctua.judicial_file_API.entity.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PersonaRepository extends JpaRepository<Persona, Long> {
    Optional<Persona> findByDni(String dni);
}
