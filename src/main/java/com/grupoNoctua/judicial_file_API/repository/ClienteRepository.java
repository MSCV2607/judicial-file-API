package com.grupoNoctua.judicial_file_API.repository;

import com.grupoNoctua.judicial_file_API.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
