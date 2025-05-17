package com.grupoNoctua.judicial_file_API.repository;

import com.grupoNoctua.judicial_file_API.entity.Expediente;
import com.grupoNoctua.judicial_file_API.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpedienteRepository extends JpaRepository<Expediente, Long> {
    List<Expediente> findByEncargadoOrColaboradoresContaining(Usuario encargado, Usuario colaborador);
}
