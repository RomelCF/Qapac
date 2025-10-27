package com.qapac.api.repository;

import com.qapac.api.domain.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarritoRepository extends JpaRepository<Carrito, Integer> {
    List<Carrito> findByCliente_IdCliente(Integer idCliente);
}
