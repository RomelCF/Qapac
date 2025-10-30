package com.qapac.api.web.dto;

import java.time.LocalDate;

public class EmpleadoDtos {
    public static class CreateEmpleadoRequest {
        public String dni;
        public String telefono;
        public String domicilio;
        public String nombres;
        public String apellidos;
        public Integer aniosExperiencia;
        public LocalDate fechaNacimiento;
        public Boolean disponible;
    }
    public static class UpdateEmpleadoRequest extends CreateEmpleadoRequest {}
}
