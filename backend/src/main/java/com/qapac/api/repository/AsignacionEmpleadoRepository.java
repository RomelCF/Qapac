package com.qapac.api.repository;

import com.qapac.api.domain.AsignacionEmpleado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AsignacionEmpleadoRepository extends JpaRepository<AsignacionEmpleado, Integer> {
    List<AsignacionEmpleado> findByAsignacionRuta_IdAsignacionRuta(Integer idAsignacionRuta);
}
