package com.qapac.api.repository;

import com.qapac.api.domain.Chofer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChoferRepository extends JpaRepository<Chofer, Integer> {
    boolean existsByEmpleado_IdEmpleado(Integer idEmpleado);
    Optional<Chofer> findByEmpleado_IdEmpleado(Integer idEmpleado);
}
