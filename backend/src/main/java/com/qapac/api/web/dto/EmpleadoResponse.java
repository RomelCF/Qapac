package com.qapac.api.web.dto;

import java.time.LocalDate;

public class EmpleadoResponse {
    public Integer idEmpleado;
    public String dni;
    public String telefono;
    public String domicilio;
    public String nombres;
    public String apellidos;
    public Integer aniosExperiencia;
    public String fechaNacimiento;
    public Boolean disponible;
    public String tipo; // "chofer", "azafato", "ninguno"
    public BreveteDto brevete; // Solo si es chofer
    
    public EmpleadoResponse(Integer idEmpleado, String dni, String telefono, String domicilio, String nombres, String apellidos, Integer aniosExperiencia, LocalDate fechaNacimiento, Boolean disponible, String tipo, BreveteDto brevete) {
        this.idEmpleado = idEmpleado;
        this.dni = dni;
        this.telefono = telefono;
        this.domicilio = domicilio;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.aniosExperiencia = aniosExperiencia;
        this.fechaNacimiento = fechaNacimiento != null ? fechaNacimiento.toString() : null;
        this.disponible = disponible != null ? disponible : true;
        this.tipo = tipo != null ? tipo : "ninguno";
        this.brevete = brevete;
    }
}
