package com.qapac.api.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateEmpresaRequest {
    @NotBlank
    private String nombre;

    @NotBlank
    @Size(min = 11, max = 11)
    @Pattern(regexp = "^\\d{11}$", message = "RUC debe tener 11 dígitos numéricos")
    private String ruc;

    @NotBlank
    private String razonSocial;

    // Asociación obligatoria por esquema; si no se envía, se responderá 400
    private Integer idUsuario;
}
