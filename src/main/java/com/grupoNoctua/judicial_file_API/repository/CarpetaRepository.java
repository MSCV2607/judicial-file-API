package com.grupoNoctua.judicial_file_API.repository;

import com.grupoNoctua.judicial_file_API.entity.Carpeta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CarpetaRepository extends JpaRepository<Carpeta, Long> {
    Optional<Carpeta> findByNumeroCarpeta(String numeroCarpeta);
}
