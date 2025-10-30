package com.qapac.api.web.dto;

import com.qapac.api.domain.enums.BusEstado;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CreateBusRequest {
    @NotBlank
    private String matricula;
    @NotNull @Min(1)
    private Integer capacidad;
    @NotNull
    private BusEstado estado;
    @NotNull
    private Integer idEmpresa;
    private List<AsientoDto> asientos;

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }
    public BusEstado getEstado() { return estado; }
    public void setEstado(BusEstado estado) { this.estado = estado; }
    public Integer getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(Integer idEmpresa) { this.idEmpresa = idEmpresa; }
    public List<AsientoDto> getAsientos() { return asientos; }
    public void setAsientos(List<AsientoDto> asientos) { this.asientos = asientos; }

    public static class AsientoDto {
        private String codigo;
        private String disponibilidad; // puede ser null para default
        public String getCodigo() { return codigo; }
        public void setCodigo(String codigo) { this.codigo = codigo; }
        public String getDisponibilidad() { return disponibilidad; }
        public void setDisponibilidad(String disponibilidad) { this.disponibilidad = disponibilidad; }
    }
}
