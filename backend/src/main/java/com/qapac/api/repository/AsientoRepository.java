package com.qapac.api.repository;

import com.qapac.api.domain.Asiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AsientoRepository extends JpaRepository<Asiento, Integer> {
    Asiento findFirstByBus_IdBusAndDisponibilidadOrderByCodigoAsc(Integer idBus, com.qapac.api.domain.enums.Disponibilidad disponibilidad);
    List<Asiento> findByBus_IdBusOrderByCodigoAsc(Integer idBus);
    long countByBus_IdBus(Integer idBus);
    long countByBus_IdBusAndDisponibilidad(Integer idBus, com.qapac.api.domain.enums.Disponibilidad disponibilidad);
}
