package com.qapac.api.repository;

import com.qapac.api.domain.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RutaRepository extends JpaRepository<Ruta, Integer> {
    java.util.List<Ruta> findByEmpresa_IdEmpresa(Integer idEmpresa);
}
