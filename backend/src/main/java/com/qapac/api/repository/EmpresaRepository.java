package com.qapac.api.repository;

import com.qapac.api.domain.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {
    Optional<Empresa> findByUsuario_IdUsuario(Integer idUsuario);
    boolean existsByUsuario_IdUsuario(Integer idUsuario);
}
