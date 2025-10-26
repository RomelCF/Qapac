package com.qapac.api.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUsuarioRequest {
    @NotBlank
    @Email
    private String correoElectronico;

    @NotBlank
    private String contrasena;
}
