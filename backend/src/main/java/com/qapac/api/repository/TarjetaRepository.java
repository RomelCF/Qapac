package com.qapac.api.repository;

import com.qapac.api.domain.Tarjeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TarjetaRepository extends JpaRepository<Tarjeta, Integer> {
    List<Tarjeta> findByCliente_IdCliente(Integer idCliente);
}
