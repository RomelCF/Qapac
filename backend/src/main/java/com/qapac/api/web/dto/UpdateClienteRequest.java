package com.qapac.api.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateClienteRequest {
    @NotBlank
    private String nombres;

    @NotBlank
    private String apellidos;

    private String domicilio;

    @NotBlank
    @Size(min = 8, max = 8)
    @Pattern(regexp = "^\\d{8}$", message = "DNI debe tener 8 dígitos numéricos")
    private String dni;

    @Pattern(regexp = "^[0-9+\\-()\\s]{7,15}$", message = "Teléfono inválido")
    private String telefono;
}
