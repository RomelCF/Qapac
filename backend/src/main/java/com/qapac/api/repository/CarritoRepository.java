package com.qapac.api.repository;

import com.qapac.api.domain.Carrito;
import com.qapac.api.domain.enums.CarritoEstado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarritoRepository extends JpaRepository<Carrito, Integer> {
    List<Carrito> findByCliente_IdCliente(Integer idCliente);
    List<Carrito> findByCliente_IdClienteAndEstado(Integer idCliente, CarritoEstado estado);
    List<Carrito> findByCliente_IdClienteAndEstadoOrderByFechaCreacionDesc(Integer idCliente, CarritoEstado estado);
}
