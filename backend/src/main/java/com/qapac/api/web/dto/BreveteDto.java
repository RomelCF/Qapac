package com.qapac.api.web.dto;

public class BreveteDto {
    public Integer idBrevete;
    public String numero;
    public String fechaEmision;
    public String fechaVencimiento;
    
    public BreveteDto(Integer idBrevete, String numero, String fechaEmision, String fechaVencimiento) {
        this.idBrevete = idBrevete;
        this.numero = numero;
        this.fechaEmision = fechaEmision;
        this.fechaVencimiento = fechaVencimiento;
    }
}
