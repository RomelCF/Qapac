package com.qapac.api.repository;

import com.qapac.api.domain.Azafato;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AzafatoRepository extends JpaRepository<Azafato, Integer> {
    boolean existsByEmpleado_IdEmpleado(Integer idEmpleado);
}
