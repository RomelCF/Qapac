package com.qapac.api.web.dto;

public class SucursalDtos {
    public static class CreateSucursalRequest {
        public String nombre;
        public String departamento;
        public String provincia;
        public String direccion;
    }
    public static class UpdateSucursalRequest extends CreateSucursalRequest {}
}
