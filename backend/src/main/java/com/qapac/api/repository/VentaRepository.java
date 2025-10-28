package com.qapac.api.repository;

import com.qapac.api.domain.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Integer> {
}
