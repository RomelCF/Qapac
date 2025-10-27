package com.qapac.api.repository;

import com.qapac.api.domain.Chofer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoferRepository extends JpaRepository<Chofer, Integer> {
    boolean existsByEmpleado_IdEmpleado(Integer idEmpleado);
}
