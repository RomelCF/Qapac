package com.qapac.api.web.dto;

import com.qapac.api.domain.enums.BusEstado;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class UpdateBusRequest {
    @NotBlank
    private String matricula;
    @NotNull @Min(1)
    private Integer capacidad;
    @NotNull
    private BusEstado estado;
    private List<CreateBusRequest.AsientoDto> asientos;

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }
    public BusEstado getEstado() { return estado; }
    public void setEstado(BusEstado estado) { this.estado = estado; }
    public List<CreateBusRequest.AsientoDto> getAsientos() { return asientos; }
    public void setAsientos(List<CreateBusRequest.AsientoDto> asientos) { this.asientos = asientos; }
}
