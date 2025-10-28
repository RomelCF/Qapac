package com.qapac.api.repository;

import com.qapac.api.domain.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {
    boolean existsByPasaje_IdCarrito(Integer idPasaje);
    long countByPasaje_AsignacionRuta_IdAsignacionRuta(Integer idAsignacionRuta);
    List<DetalleVenta> findByPasaje_Cliente_IdCliente(Integer idCliente);
    DetalleVenta findFirstByVenta_IdVenta(Integer idVenta);
}
