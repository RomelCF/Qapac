package com.qapac.api.web.dto;

public class SucursalResponse {
    public Integer idSucursal;
    public String nombre;
    public String departamento;
    public String provincia;
    public String direccion;
    public SucursalResponse(Integer idSucursal, String nombre, String departamento, String provincia, String direccion) {
        this.idSucursal = idSucursal; this.nombre = nombre; this.departamento = departamento; this.provincia = provincia; this.direccion = direccion;
    }
}
