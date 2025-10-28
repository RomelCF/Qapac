package com.qapac.api.repository;

import com.qapac.api.domain.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusRepository extends JpaRepository<Bus, Integer> {
    java.util.List<Bus> findAllByEmpresa_IdEmpresa(Integer idEmpresa);
    long countByEstadoAndEmpresa_IdEmpresa(com.qapac.api.domain.enums.BusEstado estado, Integer idEmpresa);
    long countByEmpresa_IdEmpresa(Integer idEmpresa);
}
