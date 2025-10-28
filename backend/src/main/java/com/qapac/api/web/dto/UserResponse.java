package com.qapac.api.web.dto;

public class UserResponse {
    public Integer idUsuario;
    public String correoElectronico;
    public boolean admin;
    public String tipo; // admin | cliente | empresa | none
    public UserResponse(Integer idUsuario, String correoElectronico, boolean admin, String tipo) {
        this.idUsuario = idUsuario; this.correoElectronico = correoElectronico; this.admin = admin; this.tipo = tipo;
    }
}
