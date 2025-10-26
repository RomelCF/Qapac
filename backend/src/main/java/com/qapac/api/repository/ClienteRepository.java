package com.qapac.api.repository;

import com.qapac.api.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Optional<Cliente> findByUsuario_IdUsuario(Integer idUsuario);
    boolean existsByUsuario_IdUsuario(Integer idUsuario);
}
