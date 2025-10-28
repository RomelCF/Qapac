package com.qapac.api.web.dto;

import java.math.BigDecimal;

public class RutaDtos {
    public static class CreateRutaRequest {
        public Integer idEmpresa;
        public Integer idSucursalOrigen;
        public Integer idSucursalDestino;
        public BigDecimal precio;
    }
    public static class UpdateRutaRequest extends CreateRutaRequest {}
}
