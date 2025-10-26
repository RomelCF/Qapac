package com.qapac.api.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaResponse {
    private Integer idEmpresa;
    private String nombre;
    private String ruc;
    private String razonSocial;
}
