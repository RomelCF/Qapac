package com.qapac.api.web.dto;

import com.qapac.api.domain.enums.BusEstado;

public class BusResponse {
    private Integer idBus;
    private String matricula;
    private Integer capacidad;
    private BusEstado estado;

    public BusResponse(Integer idBus, String matricula, Integer capacidad, BusEstado estado) {
        this.idBus = idBus;
        this.matricula = matricula;
        this.capacidad = capacidad;
        this.estado = estado;
    }

    public Integer getIdBus() { return idBus; }
    public String getMatricula() { return matricula; }
    public Integer getCapacidad() { return capacidad; }
    public BusEstado getEstado() { return estado; }
}
