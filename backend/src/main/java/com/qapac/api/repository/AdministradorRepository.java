package com.qapac.api.repository;

import com.qapac.api.domain.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {
    boolean existsByUsuario_IdUsuario(Integer idUsuario);
    void deleteByUsuario_IdUsuario(Integer idUsuario);
}
