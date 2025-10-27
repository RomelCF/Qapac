package com.qapac.api.repository;

import com.qapac.api.domain.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Integer> {
    List<Venta> findByCarrito_Cliente_IdCliente(Integer idCliente);
    boolean existsByCarrito_IdCarrito(Integer idCarrito);
    long countByCarrito_AsignacionRuta_IdAsignacionRuta(Integer idAsignacionRuta);
}
