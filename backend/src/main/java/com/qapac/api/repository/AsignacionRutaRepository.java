package com.qapac.api.repository;

import com.qapac.api.domain.AsignacionRuta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsignacionRutaRepository extends JpaRepository<AsignacionRuta, Integer> {
    java.util.List<AsignacionRuta> findByRuta_Empresa_IdEmpresaAndFechaPartidaBetween(Integer idEmpresa, java.time.LocalDate from, java.time.LocalDate to);
    java.util.List<AsignacionRuta> findByRuta_Empresa_IdEmpresa(Integer idEmpresa);
}
