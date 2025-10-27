package com.qapac.api.repository;

import com.qapac.api.domain.TelefonoEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelefonoEmpresaRepository extends JpaRepository<TelefonoEmpresa, Integer> {
    TelefonoEmpresa findFirstByEmpresa_IdEmpresaOrderByIdTelefonoEmpresaAsc(Integer idEmpresa);
}
