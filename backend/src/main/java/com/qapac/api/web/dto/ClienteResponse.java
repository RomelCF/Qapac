package com.qapac.api.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponse {
    private Integer idCliente;
    private String nombres;
    private String apellidos;
    private String domicilio;
    private String dni;
    private String telefono;
}
